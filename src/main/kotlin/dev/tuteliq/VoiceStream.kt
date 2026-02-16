package dev.tuteliq

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class VoiceStreamSession internal constructor(
    private val apiKey: String,
    private val config: VoiceStreamConfig?,
    private val handlers: VoiceStreamHandlers?
) {
    companion object {
        private const val VOICE_STREAM_HOST = "api.tuteliq.ai"
        private const val VOICE_STREAM_PATH = "/voice/stream"
    }

    private val json = Json { ignoreUnknownKeys = true }
    private var client: HttpClient? = null
    private var session: DefaultClientWebSocketSession? = null
    private var receiveJob: Job? = null
    private var _sessionId: String? = null
    private var _active = false
    private var summaryChannel = Channel<VoiceSessionSummaryEvent>(1)

    val sessionId: String? get() = _sessionId
    val isActive: Boolean get() = _active

    suspend fun connect() {
        client = HttpClient {
            install(WebSockets)
        }

        val readyDeferred = CompletableDeferred<Unit>()

        client!!.webSocket(
            host = VOICE_STREAM_HOST,
            port = 443,
            path = VOICE_STREAM_PATH,
            request = {
                url.protocol = io.ktor.http.URLProtocol.WSS
                headers.append("Authorization", "Bearer $apiKey")
            }
        ) {
            session = this
            _active = true

            // Send initial config if provided
            if (config != null) {
                val configMsg = buildString {
                    append("""{"type":"config"""")
                    config.intervalSeconds?.let { append(""","interval_seconds":$it""") }
                    config.analysisTypes?.let { types ->
                        append(""","analysis_types":[${types.joinToString(",") { "\"$it\"" }}]""")
                    }
                    config.context?.let { ctx ->
                        append(""","context":${json.encodeToString(VoiceStreamContext.serializer(), ctx)}""")
                    }
                    append("}")
                }
                send(Frame.Text(configMsg))
            }

            receiveJob = launch {
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                handleMessage(text, readyDeferred)
                            }
                            is Frame.Close -> {
                                val code = closeReason.await()
                                _active = false
                                handlers?.onClose?.invoke(
                                    code?.code ?: 1000,
                                    code?.message ?: ""
                                )
                            }
                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    _active = false
                    if (e !is CancellationException) {
                        handlers?.onError?.invoke(
                            VoiceErrorEvent("error", "CONNECTION_ERROR", e.message ?: "Unknown error")
                        )
                    }
                }
            }

            readyDeferred.await()

            // Keep the session alive until close is called
            receiveJob?.join()
        }
    }

    private fun handleMessage(text: String, readyDeferred: CompletableDeferred<Unit>) {
        try {
            val element = json.parseToJsonElement(text)
            val type = element.jsonObject["type"]?.jsonPrimitive?.content ?: return

            when (type) {
                "ready" -> {
                    val event = json.decodeFromString(VoiceReadyEvent.serializer(), text)
                    _sessionId = event.sessionId
                    handlers?.onReady?.invoke(event)
                    readyDeferred.complete(Unit)
                }
                "transcription" -> {
                    val event = json.decodeFromString(VoiceTranscriptionEvent.serializer(), text)
                    handlers?.onTranscription?.invoke(event)
                }
                "alert" -> {
                    val event = json.decodeFromString(VoiceAlertEvent.serializer(), text)
                    handlers?.onAlert?.invoke(event)
                }
                "session_summary" -> {
                    val event = json.decodeFromString(VoiceSessionSummaryEvent.serializer(), text)
                    handlers?.onSessionSummary?.invoke(event)
                    summaryChannel.trySend(event)
                }
                "config_updated" -> {
                    val event = json.decodeFromString(VoiceConfigUpdatedEvent.serializer(), text)
                    handlers?.onConfigUpdated?.invoke(event)
                }
                "error" -> {
                    val event = json.decodeFromString(VoiceErrorEvent.serializer(), text)
                    handlers?.onError?.invoke(event)
                }
            }
        } catch (_: Exception) {
            // Ignore non-JSON messages
        }
    }

    fun sendAudio(data: ByteArray) {
        if (!_active || session == null) {
            throw IllegalStateException("Voice stream is not connected")
        }
        runBlocking {
            session!!.send(Frame.Binary(true, data))
        }
    }

    suspend fun updateConfig(newConfig: VoiceStreamConfig) {
        if (!_active || session == null) {
            throw IllegalStateException("Voice stream is not connected")
        }
        val configMsg = json.encodeToString(
            kotlinx.serialization.serializer<Map<String, kotlinx.serialization.json.JsonElement>>(),
            buildMap {
                put("type", kotlinx.serialization.json.JsonPrimitive("config"))
                newConfig.intervalSeconds?.let {
                    put("interval_seconds", kotlinx.serialization.json.JsonPrimitive(it))
                }
                newConfig.analysisTypes?.let { types ->
                    put("analysis_types", kotlinx.serialization.json.JsonArray(
                        types.map { kotlinx.serialization.json.JsonPrimitive(it) }
                    ))
                }
                newConfig.context?.let { ctx ->
                    put("context", json.encodeToJsonElement(VoiceStreamContext.serializer(), ctx))
                }
            }
        )
        session!!.send(Frame.Text(configMsg))
    }

    suspend fun end(): VoiceSessionSummaryEvent {
        if (!_active || session == null) {
            throw IllegalStateException("Voice stream is not connected")
        }
        session!!.send(Frame.Text("""{"type":"end"}"""))
        return summaryChannel.receive()
    }

    fun close() {
        _active = false
        receiveJob?.cancel()
        runBlocking {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Client closed"))
        }
        client?.close()
        session = null
        client = null
    }
}

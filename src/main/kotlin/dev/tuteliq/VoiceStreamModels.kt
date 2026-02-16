package dev.tuteliq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceStreamConfig(
    @SerialName("interval_seconds") val intervalSeconds: Int? = null,
    @SerialName("analysis_types") val analysisTypes: List<String>? = null,
    val context: VoiceStreamContext? = null
)

@Serializable
data class VoiceStreamContext(
    val language: String? = null,
    @SerialName("age_group") val ageGroup: String? = null,
    val relationship: String? = null,
    val platform: String? = null
)

@Serializable
data class VoiceReadyEvent(
    val type: String,
    @SerialName("session_id") val sessionId: String,
    val config: VoiceStreamConfigInfo
)

@Serializable
data class VoiceStreamConfigInfo(
    @SerialName("interval_seconds") val intervalSeconds: Int,
    @SerialName("analysis_types") val analysisTypes: List<String>
)

@Serializable
data class VoiceTranscriptionSegment(
    val start: Double,
    val end: Double,
    val text: String
)

@Serializable
data class VoiceTranscriptionEvent(
    val type: String,
    val text: String,
    val segments: List<VoiceTranscriptionSegment>,
    @SerialName("flush_index") val flushIndex: Int
)

@Serializable
data class VoiceAlertEvent(
    val type: String,
    val category: String,
    val severity: String,
    @SerialName("risk_score") val riskScore: Double,
    val details: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap(),
    @SerialName("flush_index") val flushIndex: Int
)

@Serializable
data class VoiceSessionSummaryEvent(
    val type: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("duration_seconds") val durationSeconds: Double,
    @SerialName("overall_risk") val overallRisk: String,
    @SerialName("overall_risk_score") val overallRiskScore: Double,
    @SerialName("total_flushes") val totalFlushes: Int,
    val transcript: String
)

@Serializable
data class VoiceConfigUpdatedEvent(
    val type: String,
    val config: VoiceStreamConfigInfo
)

@Serializable
data class VoiceErrorEvent(
    val type: String,
    val code: String,
    val message: String
)

data class VoiceStreamHandlers(
    val onReady: ((VoiceReadyEvent) -> Unit)? = null,
    val onTranscription: ((VoiceTranscriptionEvent) -> Unit)? = null,
    val onAlert: ((VoiceAlertEvent) -> Unit)? = null,
    val onSessionSummary: ((VoiceSessionSummaryEvent) -> Unit)? = null,
    val onConfigUpdated: ((VoiceConfigUpdatedEvent) -> Unit)? = null,
    val onError: ((VoiceErrorEvent) -> Unit)? = null,
    val onClose: ((code: Short, reason: String) -> Unit)? = null
)

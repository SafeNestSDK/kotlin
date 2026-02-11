package dev.safenest

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*

/**
 * SafeNest API client for child safety analysis.
 *
 * Example:
 * ```kotlin
 * val client = SafeNest("your-api-key")
 * val result = client.detectBullying("Some text to analyze")
 * if (result.isBullying) {
 *     println("Severity: ${result.severity}")
 * }
 * client.close()
 * ```
 *
 * @property apiKey Your SafeNest API key.
 * @property timeout Request timeout in milliseconds.
 * @property maxRetries Number of retry attempts for transient failures.
 * @property retryDelay Initial retry delay in milliseconds.
 * @property baseUrl API base URL.
 */
class SafeNest(
    private val apiKey: String,
    private val timeout: Long = 30_000L,
    private val maxRetries: Int = 3,
    private val retryDelay: Long = 1_000L,
    private val baseUrl: String = "https://api.safenest.dev"
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
        }
        defaultRequest {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
        }
    }

    /** Current usage statistics (updated after each request). */
    var usage: Usage? = null
        private set

    /** Request ID from the last API call. */
    var lastRequestId: String? = null
        private set

    init {
        require(apiKey.isNotBlank()) { "API key is required" }
        require(apiKey.length >= 10) { "API key appears to be invalid" }
    }

    /**
     * Close the HTTP client.
     */
    fun close() {
        client.close()
    }

    // =========================================================================
    // Safety Detection
    // =========================================================================

    /**
     * Detect bullying in content.
     *
     * @param content Text content to analyze.
     * @param context Optional analysis context.
     * @param externalId Your identifier for correlation.
     * @param metadata Custom metadata.
     * @return BullyingResult with detection details.
     */
    suspend fun detectBullying(
        content: String,
        context: AnalysisContext? = null,
        externalId: String? = null,
        metadata: Map<String, Any>? = null
    ): BullyingResult {
        val body = buildJsonObject {
            put("text", content)
            context?.let { put("context", json.encodeToJsonElement(it)) }
            externalId?.let { put("external_id", it) }
            metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/safety/bullying", body)
    }

    /**
     * Detect bullying in content using input object.
     */
    suspend fun detectBullying(input: DetectBullyingInput): BullyingResult {
        return detectBullying(
            content = input.content,
            context = input.context,
            externalId = input.externalId,
            metadata = input.metadata
        )
    }

    /**
     * Detect grooming patterns in a conversation.
     *
     * @param input DetectGroomingInput with messages and context.
     * @return GroomingResult with detection details.
     */
    suspend fun detectGrooming(input: DetectGroomingInput): GroomingResult {
        val body = buildJsonObject {
            putJsonArray("messages") {
                input.messages.forEach { msg ->
                    addJsonObject {
                        put("sender_role", msg.role.name.lowercase())
                        put("text", msg.content)
                    }
                }
            }
            val contextObj = buildJsonObject {
                input.childAge?.let { put("child_age", it) }
                input.context?.let {
                    it.language?.let { lang -> put("language", lang) }
                    it.ageGroup?.let { ag -> put("age_group", ag) }
                    it.relationship?.let { rel -> put("relationship", rel) }
                    it.platform?.let { plat -> put("platform", plat) }
                }
            }
            if (contextObj.isNotEmpty()) {
                put("context", contextObj)
            }
            input.externalId?.let { put("external_id", it) }
            input.metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/safety/grooming", body)
    }

    /**
     * Detect unsafe content.
     *
     * @param content Text content to analyze.
     * @param context Optional analysis context.
     * @param externalId Your identifier for correlation.
     * @param metadata Custom metadata.
     * @return UnsafeResult with detection details.
     */
    suspend fun detectUnsafe(
        content: String,
        context: AnalysisContext? = null,
        externalId: String? = null,
        metadata: Map<String, Any>? = null
    ): UnsafeResult {
        val body = buildJsonObject {
            put("text", content)
            context?.let { put("context", json.encodeToJsonElement(it)) }
            externalId?.let { put("external_id", it) }
            metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/safety/unsafe", body)
    }

    /**
     * Detect unsafe content using input object.
     */
    suspend fun detectUnsafe(input: DetectUnsafeInput): UnsafeResult {
        return detectUnsafe(
            content = input.content,
            context = input.context,
            externalId = input.externalId,
            metadata = input.metadata
        )
    }

    /**
     * Quick analysis - runs bullying and unsafe detection in parallel.
     *
     * @param content Text content to analyze.
     * @param context Optional analysis context.
     * @param include Which checks to run (default: ["bullying", "unsafe"]).
     * @param externalId Your identifier for correlation.
     * @param metadata Custom metadata.
     * @return AnalyzeResult with combined results.
     */
    suspend fun analyze(
        content: String,
        context: AnalysisContext? = null,
        include: List<String>? = null,
        externalId: String? = null,
        metadata: Map<String, Any>? = null
    ): AnalyzeResult {
        val checks = include ?: listOf("bullying", "unsafe")

        var bullyingResult: BullyingResult? = null
        var unsafeResult: UnsafeResult? = null
        var maxRiskScore = 0.0

        if ("bullying" in checks) {
            bullyingResult = detectBullying(content, context, externalId, metadata)
            maxRiskScore = maxOf(maxRiskScore, bullyingResult.riskScore)
        }

        if ("unsafe" in checks) {
            unsafeResult = detectUnsafe(content, context, externalId, metadata)
            maxRiskScore = maxOf(maxRiskScore, unsafeResult.riskScore)
        }

        val riskLevel = when {
            maxRiskScore >= 0.9 -> RiskLevel.CRITICAL
            maxRiskScore >= 0.7 -> RiskLevel.HIGH
            maxRiskScore >= 0.5 -> RiskLevel.MEDIUM
            maxRiskScore >= 0.3 -> RiskLevel.LOW
            else -> RiskLevel.SAFE
        }

        val findings = mutableListOf<String>()
        if (bullyingResult?.isBullying == true) {
            findings.add("Bullying detected (${bullyingResult.severity.name.lowercase()})")
        }
        if (unsafeResult?.unsafe == true) {
            findings.add("Unsafe content: ${unsafeResult.categories.joinToString(", ")}")
        }
        val summary = if (findings.isEmpty()) "No safety concerns detected." else findings.joinToString(". ")

        val actions = listOfNotNull(
            bullyingResult?.recommendedAction,
            unsafeResult?.recommendedAction
        )
        val recommendedAction = when {
            "immediate_intervention" in actions -> "immediate_intervention"
            "flag_for_moderator" in actions -> "flag_for_moderator"
            "monitor" in actions -> "monitor"
            else -> "none"
        }

        return AnalyzeResult(
            riskLevel = riskLevel,
            riskScore = maxRiskScore,
            summary = summary,
            bullying = bullyingResult,
            unsafe = unsafeResult,
            recommendedAction = recommendedAction,
            externalId = externalId,
            metadata = metadata
        )
    }

    /**
     * Quick analysis using input object.
     */
    suspend fun analyze(input: AnalyzeInput): AnalyzeResult {
        return analyze(
            content = input.content,
            context = input.context,
            include = input.include,
            externalId = input.externalId,
            metadata = input.metadata
        )
    }

    // =========================================================================
    // Emotion Analysis
    // =========================================================================

    /**
     * Analyze emotions in content or conversation.
     *
     * @param content Text content to analyze.
     * @param context Optional analysis context.
     * @param externalId Your identifier for correlation.
     * @param metadata Custom metadata.
     * @return EmotionsResult with emotion analysis.
     */
    suspend fun analyzeEmotions(
        content: String,
        context: AnalysisContext? = null,
        externalId: String? = null,
        metadata: Map<String, Any>? = null
    ): EmotionsResult {
        val body = buildJsonObject {
            putJsonArray("messages") {
                addJsonObject {
                    put("sender", "user")
                    put("text", content)
                }
            }
            context?.let { put("context", json.encodeToJsonElement(it)) }
            externalId?.let { put("external_id", it) }
            metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/analysis/emotions", body)
    }

    /**
     * Analyze emotions using input object.
     */
    suspend fun analyzeEmotions(input: AnalyzeEmotionsInput): EmotionsResult {
        val body = buildJsonObject {
            when {
                input.content != null -> {
                    putJsonArray("messages") {
                        addJsonObject {
                            put("sender", "user")
                            put("text", input.content)
                        }
                    }
                }
                input.messages != null -> {
                    putJsonArray("messages") {
                        input.messages.forEach { msg ->
                            addJsonObject {
                                put("sender", msg.sender)
                                put("text", msg.content)
                            }
                        }
                    }
                }
            }
            input.context?.let { put("context", json.encodeToJsonElement(it)) }
            input.externalId?.let { put("external_id", it) }
            input.metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/analysis/emotions", body)
    }

    // =========================================================================
    // Guidance
    // =========================================================================

    /**
     * Get age-appropriate action guidance.
     *
     * @param input GetActionPlanInput with situation details.
     * @return ActionPlanResult with guidance steps.
     */
    suspend fun getActionPlan(input: GetActionPlanInput): ActionPlanResult {
        val body = buildJsonObject {
            put("role", (input.audience ?: Audience.PARENT).name.lowercase())
            put("situation", input.situation)
            input.childAge?.let { put("child_age", it) }
            input.severity?.let { put("severity", it.name.lowercase()) }
            input.externalId?.let { put("external_id", it) }
            input.metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/guidance/action-plan", body)
    }

    // =========================================================================
    // Reports
    // =========================================================================

    /**
     * Generate an incident report.
     *
     * @param input GenerateReportInput with messages and details.
     * @return ReportResult with incident summary.
     */
    suspend fun generateReport(input: GenerateReportInput): ReportResult {
        val body = buildJsonObject {
            putJsonArray("messages") {
                input.messages.forEach { msg ->
                    addJsonObject {
                        put("sender", msg.sender)
                        put("text", msg.content)
                    }
                }
            }
            val metaObj = buildJsonObject {
                input.childAge?.let { put("child_age", it) }
                input.incidentType?.let { put("type", it) }
            }
            if (metaObj.isNotEmpty()) {
                put("meta", metaObj)
            }
            input.externalId?.let { put("external_id", it) }
            input.metadata?.let { put("metadata", mapToJsonObject(it)) }
        }

        return request("/api/v1/reports/incident", body)
    }

    // =========================================================================
    // Account Management (GDPR)
    // =========================================================================

    /**
     * Delete all account data (GDPR Article 17 — Right to Erasure).
     */
    suspend fun deleteAccountData(): AccountDeletionResult {
        return request("DELETE", "/api/v1/account/data")
    }

    /**
     * Export all account data as JSON (GDPR Article 20 — Right to Data Portability).
     */
    suspend fun exportAccountData(): AccountExportResult {
        return request("GET", "/api/v1/account/export")
    }

    // =========================================================================
    // Private Methods
    // =========================================================================

    private suspend inline fun <reified T> request(path: String, body: JsonObject): T {
        return requestWithMethod("POST", path, body)
    }

    private suspend inline fun <reified T> request(method: String, path: String): T {
        return requestWithMethod(method, path, null)
    }

    private suspend inline fun <reified T> requestWithMethod(method: String, path: String, body: JsonObject?): T {
        var lastException: Exception? = null

        for (attempt in 0 until maxRetries) {
            try {
                return performRequest(method, path, body)
            } catch (e: AuthenticationException) {
                throw e
            } catch (e: ValidationException) {
                throw e
            } catch (e: NotFoundException) {
                throw e
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    delay(retryDelay * (1L shl attempt))
                }
            }
        }

        throw lastException ?: SafeNestException("Request failed after retries")
    }

    private suspend inline fun <reified T> performRequest(method: String, path: String, body: JsonObject?): T {
        val response: HttpResponse
        try {
            response = client.request("$baseUrl$path") {
                this.method = HttpMethod.parse(method)
                if (body != null) setBody(body)
            }
        } catch (e: HttpRequestTimeoutException) {
            throw TimeoutException("Request timed out after ${timeout}ms")
        } catch (e: Exception) {
            throw NetworkException(e.message ?: "Network error")
        }

        lastRequestId = response.headers["x-request-id"]

        // Monthly usage headers
        val limit = response.headers["x-monthly-limit"]?.toIntOrNull()
        val used = response.headers["x-monthly-used"]?.toIntOrNull()
        val remaining = response.headers["x-monthly-remaining"]?.toIntOrNull()

        if (limit != null && used != null && remaining != null) {
            usage = Usage(limit, used, remaining)
        }

        if (!response.status.isSuccess()) {
            handleErrorResponse(response)
        }

        return response.body()
    }

    private suspend fun handleErrorResponse(response: HttpResponse): Nothing {
        val status = response.status.value
        val (message, details) = try {
            val data = response.body<JsonObject>()
            val error = data["error"]?.jsonObject
            val msg = error?.get("message")?.jsonPrimitive?.content ?: "Request failed"
            val det = error?.get("details")
            msg to det
        } catch (e: Exception) {
            "Request failed" to null
        }

        throw when (status) {
            400 -> ValidationException(message, details)
            401 -> AuthenticationException(message, details)
            404 -> NotFoundException(message, details)
            429 -> RateLimitException(message, details)
            in 500..599 -> ServerException(message, status, details)
            else -> SafeNestException(message, details)
        }
    }

    private fun mapToJsonObject(map: Map<String, Any>): JsonObject {
        return buildJsonObject {
            map.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                    else -> put(key, value.toString())
                }
            }
        }
    }
}

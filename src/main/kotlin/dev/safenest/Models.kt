package dev.safenest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// =============================================================================
// Context
// =============================================================================

/**
 * Optional context for analysis.
 */
@Serializable
data class AnalysisContext(
    val language: String? = null,
    @SerialName("age_group") val ageGroup: String? = null,
    val relationship: String? = null,
    val platform: String? = null
)

// =============================================================================
// Messages
// =============================================================================

/**
 * Message for grooming detection.
 */
@Serializable
data class GroomingMessage(
    val role: MessageRole,
    val content: String
)

/**
 * Message for emotion analysis.
 */
@Serializable
data class EmotionMessage(
    val sender: String,
    val content: String
)

/**
 * Message for incident reports.
 */
@Serializable
data class ReportMessage(
    val sender: String,
    val content: String
)

// =============================================================================
// Input Types
// =============================================================================

/**
 * Input for bullying detection.
 */
data class DetectBullyingInput(
    val content: String,
    val context: AnalysisContext? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Input for grooming detection.
 */
data class DetectGroomingInput(
    val messages: List<GroomingMessage>,
    val childAge: Int? = null,
    val context: AnalysisContext? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Input for unsafe content detection.
 */
data class DetectUnsafeInput(
    val content: String,
    val context: AnalysisContext? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Input for quick analysis.
 */
data class AnalyzeInput(
    val content: String,
    val context: AnalysisContext? = null,
    val include: List<String>? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Input for emotion analysis.
 */
data class AnalyzeEmotionsInput(
    val content: String? = null,
    val messages: List<EmotionMessage>? = null,
    val context: AnalysisContext? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Input for action plan generation.
 */
data class GetActionPlanInput(
    val situation: String,
    val childAge: Int? = null,
    val audience: Audience? = null,
    val severity: Severity? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Input for incident report generation.
 */
data class GenerateReportInput(
    val messages: List<ReportMessage>,
    val childAge: Int? = null,
    val incidentType: String? = null,
    val externalId: String? = null,
    val metadata: Map<String, Any>? = null
)

// =============================================================================
// Result Types
// =============================================================================

/**
 * Result of bullying detection.
 */
@Serializable
data class BullyingResult(
    @SerialName("is_bullying") val isBullying: Boolean,
    val severity: Severity,
    @SerialName("bullying_type") val bullyingType: List<String>,
    val confidence: Double,
    val rationale: String,
    @SerialName("risk_score") val riskScore: Double,
    @SerialName("recommended_action") val recommendedAction: String,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * Result of grooming detection.
 */
@Serializable
data class GroomingResult(
    @SerialName("grooming_risk") val groomingRisk: GroomingRisk,
    val flags: List<String>,
    val confidence: Double,
    val rationale: String,
    @SerialName("risk_score") val riskScore: Double,
    @SerialName("recommended_action") val recommendedAction: String,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * Result of unsafe content detection.
 */
@Serializable
data class UnsafeResult(
    val unsafe: Boolean,
    val categories: List<String>,
    val severity: Severity,
    val confidence: Double,
    val rationale: String,
    @SerialName("risk_score") val riskScore: Double,
    @SerialName("recommended_action") val recommendedAction: String,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * Result of quick analysis.
 */
data class AnalyzeResult(
    val riskLevel: RiskLevel,
    val riskScore: Double,
    val summary: String,
    val bullying: BullyingResult?,
    val unsafe: UnsafeResult?,
    val recommendedAction: String,
    val externalId: String?,
    val metadata: Map<String, Any>?
)

/**
 * Result of emotion analysis.
 */
@Serializable
data class EmotionsResult(
    @SerialName("dominant_emotions") val dominantEmotions: List<String>,
    val trend: EmotionTrend,
    val intensity: Double,
    @SerialName("concerning_patterns") val concerningPatterns: List<String>,
    @SerialName("recommended_followup") val recommendedFollowup: String,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * Result of action plan generation.
 */
@Serializable
data class ActionPlanResult(
    val steps: List<String>,
    val tone: String,
    val resources: List<String>,
    val urgency: String,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * Result of incident report generation.
 */
@Serializable
data class ReportResult(
    val summary: String,
    @SerialName("risk_level") val riskLevel: RiskLevel,
    val timeline: List<String>,
    @SerialName("key_evidence") val keyEvidence: List<String>,
    @SerialName("recommended_next_steps") val recommendedNextSteps: List<String>,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * API usage information.
 */
data class Usage(
    val limit: Int,
    val used: Int,
    val remaining: Int
)

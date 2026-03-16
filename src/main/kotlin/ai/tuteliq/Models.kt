package ai.tuteliq

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

/**
 * Input for fraud/safety extended detection.
 */
data class DetectionInput(
    val content: String,
    val context: AnalysisContext? = null,
    val includeEvidence: Boolean = false,
    val externalId: String? = null,
    val customerId: String? = null,
    val metadata: Map<String, Any?>? = null
)

/**
 * Input for multi-endpoint analysis.
 */
data class AnalyseMultiInput(
    val content: String,
    val detections: List<Detection>,
    val context: AnalysisContext? = null,
    val includeEvidence: Boolean = false,
    val externalId: String? = null,
    val customerId: String? = null,
    val metadata: Map<String, Any?>? = null
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
    val language: String? = null,
    @SerialName("language_status") val languageStatus: String? = null,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
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
    val language: String? = null,
    @SerialName("message_analysis") val messageAnalysis: List<MessageAnalysis>? = null,
    @SerialName("language_status") val languageStatus: String? = null,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
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
    val language: String? = null,
    @SerialName("language_status") val languageStatus: String? = null,
    @SerialName("external_id") val externalId: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
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
    val metadata: Map<String, Any>?,
    val creditsUsed: Int? = null
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
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
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
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
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
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
)

// =============================================================================
// Account Management (GDPR)
// =============================================================================

/**
 * Result of account data deletion (GDPR Article 17).
 */
@Serializable
data class AccountDeletionResult(
    val message: String,
    @SerialName("deleted_count") val deletedCount: Int
)

/**
 * Result of account data export (GDPR Article 20).
 */
@Serializable
data class AccountExportResult(
    val userId: String,
    val exportedAt: String,
    val data: JsonObject
)

// =============================================================================
// Consent Management (GDPR Article 7)
// =============================================================================

@Serializable
enum class ConsentType {
    @SerialName("data_processing") DATA_PROCESSING,
    @SerialName("analytics") ANALYTICS,
    @SerialName("marketing") MARKETING,
    @SerialName("third_party_sharing") THIRD_PARTY_SHARING,
    @SerialName("child_safety_monitoring") CHILD_SAFETY_MONITORING
}

@Serializable
enum class ConsentStatus {
    @SerialName("granted") GRANTED,
    @SerialName("withdrawn") WITHDRAWN
}

data class RecordConsentInput(
    val consentType: ConsentType,
    val version: String
)

@Serializable
data class ConsentRecord(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("consent_type") val consentType: String,
    val status: String,
    val version: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class ConsentActionResult(
    val message: String,
    val consent: ConsentRecord
)

@Serializable
data class ConsentStatusResult(
    val consents: List<ConsentRecord>
)

// =============================================================================
// Right to Rectification (GDPR Article 16)
// =============================================================================

data class RectifyDataInput(
    val collection: String,
    val documentId: String,
    val fields: Map<String, Any?>
)

@Serializable
data class RectifyDataResult(
    val message: String,
    @SerialName("updated_fields") val updatedFields: List<String>
)

// =============================================================================
// Audit Logs (GDPR Article 15)
// =============================================================================

@Serializable
enum class AuditAction {
    @SerialName("data_access") DATA_ACCESS,
    @SerialName("data_export") DATA_EXPORT,
    @SerialName("data_deletion") DATA_DELETION,
    @SerialName("data_rectification") DATA_RECTIFICATION,
    @SerialName("consent_granted") CONSENT_GRANTED,
    @SerialName("consent_withdrawn") CONSENT_WITHDRAWN,
    @SerialName("breach_notification") BREACH_NOTIFICATION
}

@Serializable
data class AuditLogEntry(
    val id: String,
    @SerialName("user_id") val userId: String,
    val action: String,
    val details: JsonObject? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class AuditLogsResult(
    @SerialName("audit_logs") val auditLogs: List<AuditLogEntry>
)

/**
 * API usage information.
 */
data class Usage(
    val limit: Int,
    val used: Int,
    val remaining: Int
)

// =============================================================================
// Breach Management (GDPR Article 33/34)
// =============================================================================

@Serializable
enum class BreachSeverity {
    @SerialName("low") LOW,
    @SerialName("medium") MEDIUM,
    @SerialName("high") HIGH,
    @SerialName("critical") CRITICAL
}

@Serializable
enum class BreachStatus {
    @SerialName("detected") DETECTED,
    @SerialName("investigating") INVESTIGATING,
    @SerialName("contained") CONTAINED,
    @SerialName("reported") REPORTED,
    @SerialName("resolved") RESOLVED
}

@Serializable
enum class BreachNotificationStatus {
    @SerialName("pending") PENDING,
    @SerialName("users_notified") USERS_NOTIFIED,
    @SerialName("dpa_notified") DPA_NOTIFIED,
    @SerialName("completed") COMPLETED
}

data class LogBreachInput(
    val title: String,
    val description: String,
    val severity: BreachSeverity,
    val affectedUserIds: List<String>,
    val dataCategories: List<String>,
    val reportedBy: String
)

data class UpdateBreachInput(
    val status: BreachStatus,
    val notificationStatus: BreachNotificationStatus? = null,
    val notes: String? = null
)

@Serializable
data class BreachRecord(
    val id: String,
    val title: String,
    val description: String,
    val severity: BreachSeverity,
    val status: BreachStatus,
    @SerialName("notification_status") val notificationStatus: BreachNotificationStatus,
    @SerialName("affected_user_ids") val affectedUserIds: List<String>,
    @SerialName("data_categories") val dataCategories: List<String>,
    @SerialName("reported_by") val reportedBy: String,
    @SerialName("notification_deadline") val notificationDeadline: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class LogBreachResult(
    val message: String,
    val breach: BreachRecord
)

@Serializable
data class BreachListResult(
    val breaches: List<BreachRecord>
)

@Serializable
data class BreachResult(
    val breach: BreachRecord
)

// =============================================================================
// Detection Results (Fraud / Safety Extended / Multi)
// =============================================================================

/**
 * Per-message analysis from conversation-aware detection.
 */
@Serializable
data class MessageAnalysis(
    @SerialName("message_index") val messageIndex: Int,
    @SerialName("risk_score") val riskScore: Double,
    val flags: List<String>,
    val summary: String
)

/**
 * A detection category with tag, label, and confidence.
 */
@Serializable
data class DetectionCategory(
    val tag: String,
    val label: String,
    val confidence: Double
)

/**
 * Evidence supporting a detection result.
 */
@Serializable
data class DetectionEvidence(
    val text: String,
    val tactic: String,
    val weight: Double
)

/**
 * Age calibration applied to a detection.
 */
@Serializable
data class AgeCalibration(
    val applied: Boolean,
    @SerialName("age_group") val ageGroup: String? = null,
    val multiplier: Double? = null
)

/**
 * Result of a single detection endpoint.
 */
@Serializable
data class DetectionResult(
    val endpoint: String,
    val detected: Boolean,
    val severity: Double,
    val confidence: Double,
    @SerialName("risk_score") val riskScore: Double,
    val level: String,
    val categories: List<DetectionCategory>,
    @SerialName("recommended_action") val recommendedAction: String,
    val rationale: String,
    val language: String,
    @SerialName("language_status") val languageStatus: String,
    val evidence: List<DetectionEvidence>? = null,
    @SerialName("age_calibration") val ageCalibration: AgeCalibration? = null,
    @SerialName("message_analysis") val messageAnalysis: List<MessageAnalysis>? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null,
    @SerialName("processing_time_ms") val processingTimeMs: Double? = null,
    @SerialName("external_id") val externalId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    val metadata: JsonObject? = null
)

/**
 * Summary of multi-endpoint analysis.
 */
@Serializable
data class AnalyseMultiSummary(
    @SerialName("total_endpoints") val totalEndpoints: Int,
    @SerialName("detected_count") val detectedCount: Int,
    @SerialName("highest_risk") val highestRisk: JsonObject,
    @SerialName("overall_risk_level") val overallRiskLevel: String
)

/**
 * Result of multi-endpoint analysis.
 */
@Serializable
data class AnalyseMultiResult(
    val results: List<DetectionResult>,
    val summary: AnalyseMultiSummary,
    @SerialName("cross_endpoint_modifier") val crossEndpointModifier: Double? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null,
    @SerialName("external_id") val externalId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    val metadata: JsonObject? = null
)

// =============================================================================
// Video Analysis
// =============================================================================

/**
 * A safety finding from video frame analysis.
 */
@Serializable
data class VideoSafetyFinding(
    @SerialName("frame_index") val frameIndex: Int,
    val timestamp: Double,
    val description: String,
    val categories: List<String>,
    val severity: Double
)

/**
 * Result of video safety analysis.
 */
@Serializable
data class VideoAnalysisResult(
    @SerialName("file_id") val fileId: String? = null,
    @SerialName("frames_analyzed") val framesAnalyzed: Int,
    @SerialName("safety_findings") val safetyFindings: List<VideoSafetyFinding>,
    @SerialName("overall_risk_score") val overallRiskScore: Double,
    @SerialName("overall_severity") val overallSeverity: String,
    @SerialName("credits_used") val creditsUsed: Int? = null,
    @SerialName("external_id") val externalId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    val metadata: JsonObject? = null
)

// =============================================================================
// Voice Analysis
// =============================================================================

/**
 * A segment of a voice transcription with timestamps.
 */
@Serializable
data class TranscriptionSegment(
    val start: Double,
    val end: Double,
    val text: String
)

/**
 * Result of voice transcription.
 */
@Serializable
data class TranscriptionResult(
    val text: String,
    val language: String? = null,
    val duration: Double? = null,
    val segments: List<TranscriptionSegment>? = null
)

/**
 * Result of voice safety analysis.
 */
@Serializable
data class VoiceAnalysisResult(
    @SerialName("file_id") val fileId: String? = null,
    val transcription: TranscriptionResult? = null,
    val analysis: JsonObject? = null,
    @SerialName("overall_risk_score") val overallRiskScore: Double? = null,
    @SerialName("overall_severity") val overallSeverity: String? = null,
    @SerialName("external_id") val externalId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
)

// =============================================================================
// Image Analysis
// =============================================================================

/**
 * Vision analysis details for an image.
 */
@Serializable
data class VisionResult(
    @SerialName("extracted_text") val extractedText: String? = null,
    @SerialName("visual_categories") val visualCategories: List<String>? = null,
    @SerialName("visual_severity") val visualSeverity: String? = null,
    @SerialName("visual_confidence") val visualConfidence: Double? = null,
    @SerialName("visual_description") val visualDescription: String? = null,
    @SerialName("contains_text") val containsText: Boolean? = null,
    @SerialName("contains_faces") val containsFaces: Boolean? = null
)

/**
 * Result of image safety analysis.
 */
@Serializable
data class ImageAnalysisResult(
    @SerialName("file_id") val fileId: String? = null,
    val vision: VisionResult? = null,
    @SerialName("text_analysis") val textAnalysis: JsonObject? = null,
    @SerialName("overall_risk_score") val overallRiskScore: Double? = null,
    @SerialName("overall_severity") val overallSeverity: String? = null,
    @SerialName("external_id") val externalId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
)

// =============================================================================
// Document Analysis
// =============================================================================

/**
 * Input for document (PDF) analysis.
 */
data class AnalyzeDocumentInput(
    val file: ByteArray,
    val filename: String,
    val endpoints: List<String>? = null,
    val fileId: String? = null,
    val ageGroup: String? = null,
    val language: String? = null,
    val platform: String? = null,
    val supportThreshold: String? = null,
    val externalId: String? = null,
    val customerId: String? = null,
    val metadata: Map<String, Any?>? = null
)

/**
 * Summary of text extraction methods used across document pages.
 */
@Serializable
data class DocumentExtractionSummary(
    @SerialName("text_layer_pages") val textLayerPages: Int,
    @SerialName("ocr_pages") val ocrPages: Int,
    @SerialName("failed_pages") val failedPages: Int,
    @SerialName("average_ocr_confidence") val averageOcrConfidence: Double
)

/**
 * Detection result for a single endpoint on a single page.
 */
@Serializable
data class DocumentPageEndpointResult(
    val endpoint: String,
    val detected: Boolean,
    val severity: Double,
    val confidence: Double,
    @SerialName("risk_score") val riskScore: Double,
    val level: String,
    val categories: List<DetectionCategory>,
    val evidence: List<DetectionEvidence>,
    @SerialName("recommended_action") val recommendedAction: String,
    val rationale: String,
    @SerialName("detected_language") val detectedLanguage: String? = null
)

/**
 * Analysis result for a single document page.
 */
@Serializable
data class DocumentPageResult(
    @SerialName("page_number") val pageNumber: Int,
    @SerialName("text_preview") val textPreview: String,
    @SerialName("extraction_method") val extractionMethod: String,
    @SerialName("ocr_confidence") val ocrConfidence: Double? = null,
    val results: List<DocumentPageEndpointResult>,
    @SerialName("page_risk_score") val pageRiskScore: Double,
    @SerialName("page_severity") val pageSeverity: String
)

/**
 * A page flagged with risk score >= 0.3.
 */
@Serializable
data class DocumentFlaggedPage(
    @SerialName("page_number") val pageNumber: Int,
    @SerialName("risk_score") val riskScore: Double,
    val severity: String,
    @SerialName("detected_endpoints") val detectedEndpoints: List<String>
)

/**
 * Result of document safety analysis.
 */
@Serializable
data class DocumentAnalysisResult(
    @SerialName("file_id") val fileId: String? = null,
    @SerialName("document_hash") val documentHash: String,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("pages_analyzed") val pagesAnalyzed: Int,
    @SerialName("extraction_summary") val extractionSummary: DocumentExtractionSummary,
    @SerialName("page_results") val pageResults: List<DocumentPageResult>,
    @SerialName("overall_risk_score") val overallRiskScore: Double,
    @SerialName("overall_severity") val overallSeverity: String,
    @SerialName("detected_endpoints") val detectedEndpoints: List<String>,
    @SerialName("flagged_pages") val flaggedPages: List<DocumentFlaggedPage>,
    @SerialName("credits_used") val creditsUsed: Int,
    @SerialName("processing_time_ms") val processingTimeMs: Double,
    val language: String? = null,
    @SerialName("language_status") val languageStatus: String? = null,
    val support: JsonObject? = null,
    @SerialName("external_id") val externalId: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    val metadata: JsonObject? = null
)

// =============================================================================
// Webhooks
// =============================================================================

/**
 * A webhook configuration.
 */
@Serializable
data class Webhook(
    val id: String,
    val url: String,
    val events: List<String>,
    val active: Boolean,
    val secret: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * Result of listing webhooks.
 */
@Serializable
data class WebhookListResult(val webhooks: List<Webhook>)

/**
 * Input for creating a webhook.
 */
data class CreateWebhookInput(
    val url: String,
    val events: List<String>,
    val active: Boolean = true
)

/**
 * Result of creating a webhook.
 */
@Serializable
data class CreateWebhookResult(val message: String, val webhook: Webhook)

/**
 * Input for updating a webhook.
 */
data class UpdateWebhookInput(
    val url: String? = null,
    val events: List<String>? = null,
    val active: Boolean? = null
)

/**
 * Result of updating a webhook.
 */
@Serializable
data class UpdateWebhookResult(val message: String, val webhook: Webhook)

/**
 * Result of deleting a webhook.
 */
@Serializable
data class DeleteWebhookResult(val message: String)

/**
 * Result of testing a webhook.
 */
@Serializable
data class TestWebhookResult(
    val message: String,
    @SerialName("status_code") val statusCode: Int? = null
)

/**
 * Result of regenerating a webhook secret.
 */
@Serializable
data class RegenerateSecretResult(val message: String, val secret: String)

// =============================================================================
// Pricing
// =============================================================================

/**
 * A pricing plan summary.
 */
@Serializable
data class PricingPlan(
    val name: String,
    val price: String,
    val messages: String,
    val features: List<String>
)

/**
 * Result of listing pricing plans.
 */
@Serializable
data class PricingResult(val plans: List<PricingPlan>)

/**
 * A detailed pricing plan.
 */
@Serializable
data class PricingDetailPlan(
    val name: String,
    val tier: String,
    val price: JsonObject,
    val limits: JsonObject,
    val features: JsonObject,
    val endpoints: List<String>
)

/**
 * Result of listing detailed pricing plans.
 */
@Serializable
data class PricingDetailsResult(val plans: List<PricingDetailPlan>)

// =============================================================================
// Usage
// =============================================================================

/**
 * Usage data for a single day.
 */
@Serializable
data class UsageDay(
    val date: String,
    @SerialName("total_requests") val totalRequests: Int,
    @SerialName("success_requests") val successRequests: Int,
    @SerialName("error_requests") val errorRequests: Int
)

/**
 * Result of usage history query.
 */
@Serializable
data class UsageHistoryResult(
    @SerialName("api_key_id") val apiKeyId: String,
    val days: List<UsageDay>
)

/**
 * Result of usage breakdown by tool.
 */
@Serializable
data class UsageByToolResult(
    val date: String,
    val tools: Map<String, Int>,
    val endpoints: Map<String, Int>
)

/**
 * Result of monthly usage summary.
 */
@Serializable
data class UsageMonthlyResult(
    val tier: String,
    @SerialName("tier_display_name") val tierDisplayName: String,
    val billing: JsonObject,
    val usage: JsonObject,
    @SerialName("rate_limit") val rateLimit: JsonObject,
    val recommendations: JsonObject? = null,
    val links: JsonObject
)

// =============================================================================
// Verification
// =============================================================================

/**
 * Input for creating a verification session.
 */
data class CreateVerificationSessionInput(
    val mode: VerificationMode,
    val documentType: DocumentType? = null
)

/**
 * Verification session details.
 * The `url` property maps from the API's `mobile_url` field.
 */
@Serializable
data class VerificationSession(
    @SerialName("session_id") val sessionId: String,
    @SerialName("mobile_url") val url: String,
    @SerialName("expires_at") val expiresAt: String,
    val mode: VerificationMode,
    @SerialName("verification_mode") val verificationMode: String? = null
)

/**
 * Face match result from verification.
 */
@Serializable
data class FaceMatchResult(
    val matched: Boolean,
    val confidence: Double,
    val distance: Double? = null
)

/**
 * Liveness check result from verification.
 */
@Serializable
data class LivenessResult(
    val valid: Boolean,
    val reason: String? = null
)

/**
 * Result of age verification.
 */
@Serializable
data class AgeVerificationResult(
    @SerialName("verification_id") val verificationId: String,
    val status: VerificationStatus,
    @SerialName("age_bracket") val ageBracket: String? = null,
    @SerialName("is_minor") val isMinor: Boolean? = null,
    val liveness: LivenessResult? = null,
    @SerialName("face_match") val faceMatch: FaceMatchResult? = null,
    @SerialName("failure_reasons") val failureReasons: List<String>? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
)

/**
 * Result of identity verification.
 */
@Serializable
data class IdentityVerificationResult(
    @SerialName("verification_id") val verificationId: String,
    val status: VerificationStatus,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("document_type") val documentType: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val liveness: LivenessResult? = null,
    @SerialName("face_match") val faceMatch: FaceMatchResult? = null,
    @SerialName("credits_used") val creditsUsed: Int? = null
)

/**
 * Result of polling a verification session.
 */
@Serializable
data class VerificationSessionResult(
    @SerialName("session_id") val sessionId: String,
    val status: VerificationSessionStatus,
    val result: JsonObject? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null
)

/**
 * Result of retrieving an age verification by ID.
 */
@Serializable
data class VerificationRetrieveResult(
    @SerialName("verification_id") val verificationId: String,
    val status: VerificationStatus,
    @SerialName("age_bracket") val ageBracket: String? = null,
    @SerialName("is_minor") val isMinor: Boolean? = null,
    val liveness: LivenessResult? = null,
    @SerialName("face_match") val faceMatch: FaceMatchResult? = null,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Result of retrieving an identity verification by ID.
 */
@Serializable
data class IdentityRetrieveResult(
    @SerialName("verification_id") val verificationId: String,
    val status: VerificationStatus,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("document_type") val documentType: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val liveness: LivenessResult? = null,
    @SerialName("face_match") val faceMatch: FaceMatchResult? = null,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Result of cancelling a verification session.
 */
@Serializable
data class CancelVerificationSessionResult(
    val message: String
)

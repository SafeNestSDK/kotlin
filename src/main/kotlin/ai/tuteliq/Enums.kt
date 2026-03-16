package ai.tuteliq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Severity level for detected issues.
 */
@Serializable
enum class Severity {
    @SerialName("low") LOW,
    @SerialName("medium") MEDIUM,
    @SerialName("high") HIGH,
    @SerialName("critical") CRITICAL
}

/**
 * Risk level for grooming detection.
 */
@Serializable
enum class GroomingRisk {
    @SerialName("none") NONE,
    @SerialName("low") LOW,
    @SerialName("medium") MEDIUM,
    @SerialName("high") HIGH,
    @SerialName("critical") CRITICAL
}

/**
 * Overall risk level for content analysis.
 */
@Serializable
enum class RiskLevel {
    @SerialName("safe") SAFE,
    @SerialName("low") LOW,
    @SerialName("medium") MEDIUM,
    @SerialName("high") HIGH,
    @SerialName("critical") CRITICAL
}

/**
 * Emotion trend direction.
 */
@Serializable
enum class EmotionTrend {
    @SerialName("improving") IMPROVING,
    @SerialName("stable") STABLE,
    @SerialName("worsening") WORSENING
}

/**
 * Target audience for action plans.
 */
@Serializable
enum class Audience {
    @SerialName("child") CHILD,
    @SerialName("parent") PARENT,
    @SerialName("educator") EDUCATOR,
    @SerialName("platform") PLATFORM
}

/**
 * Role of message sender in grooming detection.
 */
@Serializable
enum class MessageRole {
    @SerialName("adult") ADULT,
    @SerialName("child") CHILD,
    @SerialName("unknown") UNKNOWN
}

/**
 * Supported languages for analysis.
 */
@Serializable
enum class Language(@Suppress("unused") val code: String) {
    @SerialName("en") EN("en"),
    @SerialName("es") ES("es"),
    @SerialName("pt") PT("pt"),
    @SerialName("uk") UK("uk"),
    @SerialName("sv") SV("sv"),
    @SerialName("no") NO("no"),
    @SerialName("da") DA("da"),
    @SerialName("fi") FI("fi"),
    @SerialName("de") DE("de"),
    @SerialName("fr") FR("fr");

    companion object {
        val all: List<Language> = entries.toList()
    }
}

/**
 * Status of language support.
 */
@Serializable
enum class LanguageStatus {
    @SerialName("stable") STABLE,
    @SerialName("beta") BETA
}

/**
 * Detection endpoint types.
 */
@Serializable
enum class Detection(@Suppress("unused") val endpoint: String) {
    @SerialName("bullying") BULLYING("bullying"),
    @SerialName("grooming") GROOMING("grooming"),
    @SerialName("unsafe") UNSAFE("unsafe"),
    @SerialName("social-engineering") SOCIAL_ENGINEERING("social-engineering"),
    @SerialName("app-fraud") APP_FRAUD("app-fraud"),
    @SerialName("romance-scam") ROMANCE_SCAM("romance-scam"),
    @SerialName("mule-recruitment") MULE_RECRUITMENT("mule-recruitment"),
    @SerialName("gambling-harm") GAMBLING_HARM("gambling-harm"),
    @SerialName("coercive-control") COERCIVE_CONTROL("coercive-control"),
    @SerialName("vulnerability-exploitation") VULNERABILITY_EXPLOITATION("vulnerability-exploitation"),
    @SerialName("radicalisation") RADICALISATION("radicalisation")
}

/**
 * Detection endpoints available for document analysis.
 */
@Serializable
enum class DocumentEndpointName(@Suppress("unused") val endpoint: String) {
    @SerialName("unsafe") UNSAFE("unsafe"),
    @SerialName("bullying") BULLYING("bullying"),
    @SerialName("grooming") GROOMING("grooming"),
    @SerialName("social-engineering") SOCIAL_ENGINEERING("social-engineering"),
    @SerialName("coercive-control") COERCIVE_CONTROL("coercive-control"),
    @SerialName("radicalisation") RADICALISATION("radicalisation"),
    @SerialName("romance-scam") ROMANCE_SCAM("romance-scam"),
    @SerialName("mule-recruitment") MULE_RECRUITMENT("mule-recruitment")
}

/**
 * Subscription tier levels.
 */
@Serializable
enum class Tier {
    @SerialName("starter") STARTER,
    @SerialName("indie") INDIE,
    @SerialName("pro") PRO,
    @SerialName("business") BUSINESS,
    @SerialName("enterprise") ENTERPRISE
}

/**
 * Verification mode for age/identity verification.
 */
@Serializable
enum class VerificationMode {
    @SerialName("age") AGE,
    @SerialName("identity") IDENTITY
}

/**
 * Document types accepted for verification.
 */
@Serializable
enum class DocumentType {
    @SerialName("passport") PASSPORT,
    @SerialName("id_card") ID_CARD,
    @SerialName("drivers_license") DRIVERS_LICENSE
}

/**
 * Verification result status.
 */
@Serializable
enum class VerificationStatus {
    @SerialName("verified") VERIFIED,
    @SerialName("failed") FAILED,
    @SerialName("needs_review") NEEDS_REVIEW
}

/**
 * Verification session status.
 */
@Serializable
enum class VerificationSessionStatus {
    @SerialName("pending") PENDING,
    @SerialName("in_progress") IN_PROGRESS,
    @SerialName("completed") COMPLETED,
    @SerialName("failed") FAILED,
    @SerialName("expired") EXPIRED,
    @SerialName("cancelled") CANCELLED
}

<p align="center">
  <img src="./assets/logo.png" alt="SafeNest" width="200" />
</p>

<h1 align="center">SafeNest Kotlin SDK</h1>

<p align="center">
  <strong>Official Kotlin SDK for the SafeNest API</strong><br>
  AI-powered child safety analysis
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/dev.safenest/safenest"><img src="https://img.shields.io/maven-central/v/dev.safenest/safenest.svg" alt="Maven Central"></a>
  <a href="https://github.com/SafeNestSDK/kotlin/actions"><img src="https://img.shields.io/github/actions/workflow/status/SafeNestSDK/kotlin/ci.yml" alt="build status"></a>
  <a href="https://github.com/SafeNestSDK/kotlin/blob/main/LICENSE"><img src="https://img.shields.io/github/license/SafeNestSDK/kotlin.svg" alt="license"></a>
</p>

<p align="center">
  <a href="https://api.safenest.dev/docs">API Docs</a> •
  <a href="https://safenest.app">Dashboard</a> •
  <a href="https://discord.gg/7kbTeRYRXD">Discord</a>
</p>

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("dev.safenest:safenest:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'dev.safenest:safenest:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>dev.safenest</groupId>
    <artifactId>safenest</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Requirements

- Kotlin 1.9+
- Java 17+

---

## Quick Start

```kotlin
import dev.safenest.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = SafeNest(apiKey = "your-api-key")

    // Quick safety analysis
    val result = client.analyze("Message to check")

    if (result.riskLevel != RiskLevel.SAFE) {
        println("Risk: ${result.riskLevel}")
        println("Summary: ${result.summary}")
    }

    client.close()
}
```

---

## API Reference

### Initialization

```kotlin
import dev.safenest.SafeNest

// Simple
val client = SafeNest(apiKey = "your-api-key")

// With options
val client = SafeNest(
    apiKey = "your-api-key",
    timeout = 30_000L,     // Request timeout in milliseconds
    maxRetries = 3,        // Retry attempts
    retryDelay = 1_000L,   // Initial retry delay in milliseconds
)
```

### Bullying Detection

```kotlin
val result = client.detectBullying("Nobody likes you, just leave")

if (result.isBullying) {
    println("Severity: ${result.severity}")       // Severity.MEDIUM
    println("Types: ${result.bullyingType}")      // ["exclusion", "verbal_abuse"]
    println("Confidence: ${result.confidence}")   // 0.92
    println("Rationale: ${result.rationale}")
}
```

### Grooming Detection

```kotlin
import dev.safenest.*

val result = client.detectGrooming(
    DetectGroomingInput(
        messages = listOf(
            GroomingMessage(role = MessageRole.ADULT, content = "This is our secret"),
            GroomingMessage(role = MessageRole.CHILD, content = "Ok I won't tell"),
        ),
        childAge = 12,
    )
)

if (result.groomingRisk == GroomingRisk.HIGH) {
    println("Flags: ${result.flags}")  // ["secrecy", "isolation"]
}
```

### Unsafe Content Detection

```kotlin
val result = client.detectUnsafe("I don't want to be here anymore")

if (result.unsafe) {
    println("Categories: ${result.categories}")  // ["self_harm", "crisis"]
    println("Severity: ${result.severity}")      // Severity.CRITICAL
}
```

### Quick Analysis

Runs bullying and unsafe detection:

```kotlin
val result = client.analyze("Message to check")

println("Risk Level: ${result.riskLevel}")   // RiskLevel.SAFE/LOW/MEDIUM/HIGH/CRITICAL
println("Risk Score: ${result.riskScore}")   // 0.0 - 1.0
println("Summary: ${result.summary}")
println("Action: ${result.recommendedAction}")
```

### Emotion Analysis

```kotlin
val result = client.analyzeEmotions("I'm so stressed about everything")

println("Emotions: ${result.dominantEmotions}")  // ["anxiety", "sadness"]
println("Trend: ${result.trend}")                 // EmotionTrend.WORSENING
println("Followup: ${result.recommendedFollowup}")
```

### Action Plan

```kotlin
import dev.safenest.*

val plan = client.getActionPlan(
    GetActionPlanInput(
        situation = "Someone is spreading rumors about me",
        childAge = 12,
        audience = Audience.CHILD,
        severity = Severity.MEDIUM,
    )
)

println("Steps: ${plan.steps}")
println("Tone: ${plan.tone}")
```

### Incident Report

```kotlin
import dev.safenest.*

val report = client.generateReport(
    GenerateReportInput(
        messages = listOf(
            ReportMessage(sender = "user1", content = "Threatening message"),
            ReportMessage(sender = "child", content = "Please stop"),
        ),
        childAge = 14,
    )
)

println("Summary: ${report.summary}")
println("Risk: ${report.riskLevel}")
println("Next Steps: ${report.recommendedNextSteps}")
```

---

## Tracking Fields

All methods support `externalId` and `metadata` for correlating requests:

```kotlin
val result = client.detectBullying(
    content = "Test message",
    externalId = "msg_12345",
    metadata = mapOf("user_id" to "usr_abc", "session" to "sess_xyz"),
)

// Echoed back in response
println(result.externalId)  // "msg_12345"
println(result.metadata)    // {"user_id": "usr_abc", ...}
```

---

## Usage Tracking

```kotlin
val result = client.detectBullying("test")

// Access usage stats after any request
client.usage?.let { usage ->
    println("Limit: ${usage.limit}")
    println("Used: ${usage.used}")
    println("Remaining: ${usage.remaining}")
}

// Request metadata
println("Request ID: ${client.lastRequestId}")
```

---

## Error Handling

```kotlin
import dev.safenest.*

try {
    val result = client.detectBullying("test")
} catch (e: AuthenticationException) {
    println("Auth error: ${e.message}")
} catch (e: RateLimitException) {
    println("Rate limited: ${e.message}")
} catch (e: ValidationException) {
    println("Invalid input: ${e.message}, details: ${e.details}")
} catch (e: ServerException) {
    println("Server error ${e.statusCode}: ${e.message}")
} catch (e: TimeoutException) {
    println("Timeout: ${e.message}")
} catch (e: NetworkException) {
    println("Network error: ${e.message}")
} catch (e: SafeNestException) {
    println("Error: ${e.message}")
}
```

---

## Android Example

```kotlin
import dev.safenest.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SafetyChecker(private val apiKey: String) {
    private val client = SafeNest(apiKey = apiKey)

    suspend fun checkMessage(message: String): AnalyzeResult {
        return withContext(Dispatchers.IO) {
            client.analyze(message)
        }
    }

    fun close() {
        client.close()
    }
}
```

---

## Support

- **API Docs**: [api.safenest.dev/docs](https://api.safenest.dev/docs)
- **Discord**: [discord.gg/7kbTeRYRXD](https://discord.gg/7kbTeRYRXD)
- **Email**: support@safenest.dev
- **Issues**: [GitHub Issues](https://github.com/SafeNestSDK/kotlin/issues)

---

## License

MIT License - see [LICENSE](LICENSE) for details.

---

<p align="center">
  <sub>Built with care for child safety by the <a href="https://safenest.dev">SafeNest</a> team</sub>
</p>

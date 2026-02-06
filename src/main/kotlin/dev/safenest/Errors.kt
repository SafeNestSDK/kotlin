package dev.safenest

/**
 * Base exception for SafeNest SDK errors.
 */
open class SafeNestException(
    override val message: String,
    val details: Any? = null
) : Exception(message)

/**
 * Thrown when API key is invalid or missing.
 */
class AuthenticationException(
    message: String,
    details: Any? = null
) : SafeNestException(message, details)

/**
 * Thrown when rate limit is exceeded.
 */
class RateLimitException(
    message: String,
    details: Any? = null
) : SafeNestException(message, details)

/**
 * Thrown when request validation fails.
 */
class ValidationException(
    message: String,
    details: Any? = null
) : SafeNestException(message, details)

/**
 * Thrown when a resource is not found.
 */
class NotFoundException(
    message: String,
    details: Any? = null
) : SafeNestException(message, details)

/**
 * Thrown when the server returns a 5xx error.
 */
class ServerException(
    message: String,
    val statusCode: Int,
    details: Any? = null
) : SafeNestException(message, details)

/**
 * Thrown when a request times out.
 */
class TimeoutException(
    message: String,
    details: Any? = null
) : SafeNestException(message, details)

/**
 * Thrown when a network error occurs.
 */
class NetworkException(
    message: String,
    details: Any? = null
) : SafeNestException(message, details)

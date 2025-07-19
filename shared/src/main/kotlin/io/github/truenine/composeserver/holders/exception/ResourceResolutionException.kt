package io.github.truenine.composeserver.holders.exception

/**
 * Base exception for resource resolution errors.
 *
 * This exception hierarchy provides specific error types for different resource resolution failures, enabling better error handling and debugging.
 *
 * @author TrueNine
 * @since 2024-07-18
 */
open class ResourceResolutionException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** Exception thrown when a resource source configuration is invalid. */
class InvalidResourceSourceException(message: String, cause: Throwable? = null) : ResourceResolutionException(message, cause)

/** Exception thrown when a resource pattern is invalid or malformed. */
class InvalidResourcePatternException(pattern: String, cause: Throwable? = null) : ResourceResolutionException("Invalid resource pattern: $pattern", cause)

/** Exception thrown when resource resolution fails due to I/O errors. */
class ResourceIOException(message: String, cause: Throwable? = null) : ResourceResolutionException(message, cause)

/** Exception thrown when required resources are not found and no fallback is available. */
class ResourceNotFoundException(pattern: String, profile: String? = null, cause: Throwable? = null) :
  ResourceResolutionException("Resource not found: $pattern${if (profile != null) " (profile: $profile)" else ""}", cause)

/** Exception thrown when resource configuration validation fails. */
class ResourceConfigurationException(message: String, cause: Throwable? = null) : ResourceResolutionException(message, cause)

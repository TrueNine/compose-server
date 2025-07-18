package io.github.truenine.composeserver.holders.config

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Enhanced configuration properties for ResourceHolder with Spring Boot-like features.
 *
 * This configuration class extends the basic DataLoadProperties with additional features for robust resource management:
 * - Profile-based resource resolution
 * - Custom resource source definitions
 * - Caching configuration
 * - Validation rules
 * - Performance tuning options
 *
 * @author TrueNine
 * @since 2024-07-18
 */
@Validated
@ConfigurationProperties(prefix = "compose.shared.resource-holder")
data class ResourceHolderConfiguration(

  // Basic configuration (backward compatibility)
  @field:NotBlank(message = "Config location cannot be blank") val configLocation: String = "config",
  @field:NotBlank(message = "Data location cannot be blank") val location: String = "data",
  @field:NotEmpty(message = "Match files list cannot be empty") val matchFiles: MutableList<String> = mutableListOf(),

  // Enhanced configuration
  /** Active profiles for environment-specific resource resolution. Resources with matching profiles take precedence over default resources. */
  val activeProfiles: List<String> = emptyList(),

  /** Custom resource sources with explicit priorities. These sources are added to the default Spring Boot-like hierarchy. */
  val customSources: List<CustomResourceSource> = emptyList(),

  /** Cache configuration for performance optimization. */
  val cache: CacheConfiguration = CacheConfiguration(),

  /** Whether to enable strict mode for resource resolution. In strict mode, missing required resources will cause startup failure. */
  val strictMode: Boolean = false,

  /** Whether to enable resource change detection and automatic cache invalidation. */
  val enableChangeDetection: Boolean = false,

  /** Timeout in milliseconds for resource resolution operations. */
  @field:Min(value = 1000, message = "Resolution timeout must be at least 1000ms") val resolutionTimeoutMs: Long = 30000,

  /** Whether to enable detailed logging for resource resolution debugging. */
  val enableDebugLogging: Boolean = false,

  /** Fallback configuration for when resources are not found. */
  val fallback: FallbackConfiguration = FallbackConfiguration(),
) {

  /** Gets the primary active profile (first in the list). */
  val primaryProfile: String?
    get() = activeProfiles.firstOrNull()

  /** Checks if a profile is active. */
  fun isProfileActive(profile: String): Boolean {
    return activeProfiles.contains(profile)
  }

  /** Gets all resource locations including custom sources. */
  fun getAllResourceLocations(): List<String> {
    val locations = mutableListOf(location)
    locations.addAll(customSources.map { it.path })
    return locations.distinct()
  }
}

/** Configuration for custom resource sources. */
data class CustomResourceSource(
  @field:NotBlank(message = "Resource source type cannot be blank") val type: String,
  @field:NotBlank(message = "Resource source path cannot be blank") val path: String,
  @field:Min(value = 0, message = "Priority must be non-negative") val priority: Int,
  val profile: String? = null,
  val description: String = "",
) {

  /** Converts to ResourceSource. */
  fun toResourceSource(): ResourceSource {
    val resourceType =
      when (type.lowercase()) {
        "filesystem",
        "file" -> ResourceType.FILESYSTEM
        "classpath",
        "cp" -> ResourceType.CLASSPATH
        "url",
        "http",
        "https" -> ResourceType.URL
        else -> throw IllegalArgumentException("Unsupported resource type: $type")
      }

    return ResourceSource(type = resourceType, path = path, priority = priority, profile = profile, description = description.ifBlank { "$type: $path" })
  }
}

/** Cache configuration for resource resolution. */
data class CacheConfiguration(
  /** Whether caching is enabled. */
  val enabled: Boolean = true,

  /** Maximum number of entries in the cache. */
  @field:Min(value = 10, message = "Cache max size must be at least 10") val maxSize: Int = 1000,

  /** Time-to-live for cache entries in milliseconds. */
  @field:Min(value = 1000, message = "Cache TTL must be at least 1000ms") val ttlMs: Long = 300_000, // 5 minutes

  /** Whether to enable cache statistics collection. */
  val enableStats: Boolean = true,

  /** Interval in milliseconds for automatic cleanup of expired entries. */
  @field:Min(value = 10000, message = "Cleanup interval must be at least 10000ms") val cleanupIntervalMs: Long = 60_000, // 1 minute
)

/** Fallback configuration for resource resolution. */
data class FallbackConfiguration(
  /** Whether to enable fallback to default resources when profile-specific resources are not found. */
  val enableProfileFallback: Boolean = true,

  /** Whether to enable fallback to classpath resources when filesystem resources are not found. */
  val enableClasspathFallback: Boolean = true,

  /** Default resource patterns to use as fallbacks. */
  val defaultPatterns: List<String> = emptyList(),

  /** Whether to create empty resources when no fallbacks are available. */
  val createEmptyResources: Boolean = false,
)

/** Validation configuration for resource patterns and sources. */
data class ValidationConfiguration(
  /** Whether to validate resource patterns for security (e.g., prevent path traversal). */
  val enablePatternValidation: Boolean = true,

  /** Whether to validate that configured resource sources are accessible at startup. */
  val enableSourceValidation: Boolean = true,

  /** List of allowed resource patterns (regex). */
  val allowedPatterns: List<String> = listOf(".*"),

  /** List of forbidden resource patterns (regex). */
  val forbiddenPatterns: List<String> = listOf(".*\\.\\./.*"), // Prevent path traversal
)

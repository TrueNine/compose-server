package io.github.truenine.composeserver.holders.config

import io.github.truenine.composeserver.hasText
import io.github.truenine.composeserver.holders.config.ResourceSource.Companion.url
import io.github.truenine.composeserver.holders.exception.InvalidResourceSourceException
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

/**
 * Represents a source of resources with metadata for precedence and profile-based resolution.
 *
 * This class encapsulates the configuration for different resource locations following Spring Boot's property source hierarchy pattern.
 *
 * @property type The type of resource source (FILESYSTEM, CLASSPATH, URL)
 * @property path The base path for this resource source
 * @property priority Priority for resource resolution (higher values = higher priority)
 * @property profile Optional profile name for environment-specific resources
 * @property description Human-readable description of this resource source
 * @author TrueNine
 * @since 2024-07-18
 */
data class ResourceSource(var type: ResourceType, var path: String, var priority: Int, var profile: String? = null, var description: String = "") {

  init {
    if (!path.hasText()) {
      throw InvalidResourceSourceException("Resource source path cannot be empty")
    }
    if (priority < 0) {
      throw InvalidResourceSourceException("Resource source priority must be non-negative: $priority")
    }
  }

  /**
   * Resolves a resource pattern within this source.
   *
   * @param pattern The resource pattern to resolve
   * @param resolver The resource pattern resolver to use
   * @return List of resolved resources
   */
  fun resolveResources(pattern: String, resolver: PathMatchingResourcePatternResolver): List<Resource> {
    if (!pattern.hasText()) return emptyList()

    return try {
      val fullPattern = buildFullPattern(pattern)
      resolver.getResources(fullPattern).filter { it.exists() }.toList()
    } catch (e: Exception) {
      // Log error but don't fail - allow graceful degradation
      emptyList()
    }
  }

  /**
   * Builds the full resource pattern for this source.
   *
   * @param pattern The base pattern
   * @return The full pattern including source path and profile
   */
  private fun buildFullPattern(pattern: String): String {
    val normalizedPath = normalizePath(path)
    val profilePath = if (profile.hasText()) "/$profile" else ""

    return when (type) {
      ResourceType.FILESYSTEM -> "file:$normalizedPath$profilePath/$pattern"
      ResourceType.CLASSPATH -> "classpath:$normalizedPath$profilePath/$pattern"
      ResourceType.URL -> "$normalizedPath$profilePath/$pattern"
    }
  }

  /** Normalizes the path by removing redundant separators and ensuring proper format. */
  private fun normalizePath(path: String): String {
    val normalized = path.replace("\\", "/")

    // For URLs, preserve the protocol double slash
    return if (normalized.contains("://")) {
      normalized.replace(Regex("(?<!:)//+"), "/").removeSuffix("/")
    } else {
      normalized.replace(Regex("/+"), "/").removeSuffix("/")
    }
  }

  /**
   * Checks if this resource source matches the given profile.
   *
   * @param targetProfile The profile to match against
   * @return true if this source applies to the target profile
   */
  fun matchesProfile(targetProfile: String?): Boolean {
    return profile == null || profile == targetProfile
  }

  companion object {

    /**
     * Creates a filesystem resource source.
     *
     * @param path The filesystem path
     * @param priority The priority for this source
     * @param profile Optional profile name
     * @return A new filesystem resource source
     */
    fun filesystem(path: String, priority: Int, profile: String? = null): ResourceSource {
      return ResourceSource(
        type = ResourceType.FILESYSTEM,
        path = path,
        priority = priority,
        profile = profile,
        description = "Filesystem: $path${if (profile != null) " (profile: $profile)" else ""}",
      )
    }

    /**
     * Creates a classpath resource source.
     *
     * @param path The classpath path
     * @param priority The priority for this source
     * @param profile Optional profile name
     * @return A new classpath resource source
     */
    fun classpath(path: String, priority: Int, profile: String? = null): ResourceSource {
      return ResourceSource(
        type = ResourceType.CLASSPATH,
        path = path,
        priority = priority,
        profile = profile,
        description = "Classpath: $path${if (profile != null) " (profile: $profile)" else ""}",
      )
    }

    /**
     * Creates a URL resource source.
     *
     * @param url The URL
     * @param priority The priority for this source
     * @param profile Optional profile name
     * @return A new URL resource source
     */
    fun url(url: String, priority: Int, profile: String? = null): ResourceSource {
      return ResourceSource(
        type = ResourceType.URL,
        path = url,
        priority = priority,
        profile = profile,
        description = "URL: $url${if (profile != null) " (profile: $profile)" else ""}",
      )
    }

    /**
     * Creates default Spring Boot-like resource sources for the given configuration.
     *
     * @param configLocation The config location (e.g., "config")
     * @param dataLocation The data location (e.g., "data")
     * @param applicationHome The application home directory
     * @param profile Optional active profile
     * @return List of resource sources in priority order (highest first)
     */
    fun createDefaultSources(configLocation: String, dataLocation: String, applicationHome: String, profile: String? = null): List<ResourceSource> {
      val sources = mutableListOf<ResourceSource>()

      // External filesystem sources (highest priority)
      sources.add(filesystem("$applicationHome/$configLocation/$dataLocation", 1000, profile))
      sources.add(filesystem("$applicationHome/$configLocation/$dataLocation", 900))
      sources.add(filesystem("$applicationHome/$dataLocation", 800, profile))
      sources.add(filesystem("$applicationHome/$dataLocation", 700))

      // Classpath sources (lower priority)
      sources.add(classpath("$configLocation/$dataLocation", 600, profile))
      sources.add(classpath("$configLocation/$dataLocation", 500))
      sources.add(classpath(dataLocation, 400, profile))
      sources.add(classpath(dataLocation, 300))

      return sources.sortedByDescending { it.priority }
    }
  }
}

/** Enumeration of supported resource types. */
enum class ResourceType {
  /** Filesystem-based resources */
  FILESYSTEM,

  /** Classpath-based resources */
  CLASSPATH,

  /** URL-based resources */
  URL,
}

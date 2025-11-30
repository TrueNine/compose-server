package io.github.truenine.composeserver.holders.resolver

import io.github.truenine.composeserver.holders.config.FallbackConfiguration
import io.github.truenine.composeserver.holders.config.ResourceSource
import io.github.truenine.composeserver.holders.exception.InvalidResourcePatternException
import io.github.truenine.composeserver.holders.exception.ResourceIOException
import io.github.truenine.composeserver.holders.fallback.ResourceFallbackHandler
import io.github.truenine.composeserver.holders.validation.ResourceValidator
import io.github.truenine.composeserver.logger
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.util.concurrent.ConcurrentHashMap

/**
 * Core resource resolver that handles resource resolution with precedence and caching.
 *
 * This class implements the Spring Boot-like resource resolution strategy with:
 * - Property source hierarchy with clear precedence
 * - Profile-aware resource resolution
 * - Caching for performance optimization
 * - Robust error handling with fallbacks
 * - Thread-safe operations
 *
 * @property resourceSources List of resource sources in priority order
 * @property resourceLoader Spring resource loader
 * @property cache Resource cache for performance optimization
 * @author TrueNine
 * @since 2024-07-18
 */
class ResourceResolver(
  private val resourceSources: List<ResourceSource>,
  private val resourceLoader: ResourceLoader,
  private val cache: ResourceCache = ResourceCache(),
  private val fallbackConfig: FallbackConfiguration = FallbackConfiguration(),
) {

  companion object {
    private val log = logger<ResourceResolver>()
  }

  // Thread-safe resolver pool to avoid creating new instances
  private val resolverPool = ConcurrentHashMap<String, PathMatchingResourcePatternResolver>()

  // Fallback handler for graceful degradation
  private val fallbackHandler: ResourceFallbackHandler

  init {
    log.info("Initialized ResourceResolver with {} resource sources", resourceSources.size)
    resourceSources.forEachIndexed { index, source -> log.debug("Resource source {}: {} (priority: {})", index + 1, source.description, source.priority) }

    // Validate resource sources
    try {
      ResourceValidator.validateResourceSources(resourceSources)
    } catch (e: Exception) {
      log.warn("Resource source validation failed: {}", e.message)
    }

    // Initialize fallback handler
    fallbackHandler = ResourceFallbackHandler(fallbackConfig, getResolver("default"))
    log.info("Fallback strategies enabled: {}", fallbackHandler.getFallbackStrategySummary())
  }

  /**
   * Resolves the first matching resource for the given pattern and profile.
   *
   * @param pattern The resource pattern to resolve
   * @param profile Optional profile for environment-specific resolution
   * @return The first matching resource or null if not found
   */
  fun resolveResource(pattern: String, profile: String? = null): Resource? {
    return resolveResources(pattern, profile).firstOrNull()
  }

  /**
   * Resolves all matching resources for the given pattern and profile.
   *
   * Resources are returned in precedence order (highest priority first). Duplicate resources (same filename) are filtered to keep only the highest priority
   * version.
   *
   * @param pattern The resource pattern to resolve
   * @param profile Optional profile for environment-specific resolution
   * @return List of matching resources in priority order
   */
  fun resolveResources(pattern: String, profile: String? = null): List<Resource> {
    // Validate pattern first (this will throw exception for empty/invalid patterns)
    validatePattern(pattern)

    // Check cache first
    cache.get(pattern, profile)?.let { cachedResources ->
      log.debug("Returning {} cached resources for pattern: {} (profile: {})", cachedResources.size, pattern, profile)
      return cachedResources
    }

    try {
      val resolvedResources = doResolveResources(pattern, profile)

      // Cache the results
      cache.put(pattern, profile, resolvedResources)

      log.debug("Resolved {} resources for pattern: {} (profile: {})", resolvedResources.size, pattern, profile)

      return resolvedResources
    } catch (e: Exception) {
      log.error("Failed to resolve resources for pattern: {} (profile: {})", pattern, profile, e)
      throw ResourceIOException("Resource resolution failed for pattern: $pattern", e)
    }
  }

  /** Performs the actual resource resolution without caching. */
  private fun doResolveResources(pattern: String, profile: String?): List<Resource> {
    val allResources = mutableListOf<ResourceWithMetadata>()

    // Get applicable sources for the profile
    val applicableSources = resourceSources.filter { it.matchesProfile(profile) }.sortedByDescending { it.priority }

    log.debug("Using {} applicable resource sources for profile: {}", applicableSources.size, profile)

    // Resolve resources from each source
    for (source in applicableSources) {
      try {
        val resolver = getResolver(source.type.name)
        val resources = source.resolveResources(pattern, resolver)

        resources.forEach { resource -> allResources.add(ResourceWithMetadata(resource, source)) }

        if (resources.isNotEmpty()) {
          log.debug("Found {} resources from source: {}", resources.size, source.description)
        }
      } catch (e: Exception) {
        log.warn("Failed to resolve resources from source: {} - {}", source.description, e.message)
        // Continue with other sources - graceful degradation
      }
    }

    // Remove duplicates, keeping highest priority version
    val resolvedResources = removeDuplicates(allResources)

    // If no resources found and fallback is enabled, try fallback strategies
    if (resolvedResources.isEmpty() && fallbackHandler.isFallbackEnabled()) {
      log.debug("No resources found, attempting fallback resolution")
      val fallbackResources = fallbackHandler.handleFallback(pattern, profile, resourceSources)
      return fallbackResources
    }

    return resolvedResources
  }

  /** Removes duplicate resources, keeping the highest priority version of each. */
  private fun removeDuplicates(resources: List<ResourceWithMetadata>): List<Resource> {
    val resourceMap = mutableMapOf<String, ResourceWithMetadata>()

    for (resourceWithMetadata in resources) {
      val resource = resourceWithMetadata.resource
      val key = generateResourceKey(resource)

      val existing = resourceMap[key]
      if (existing == null || resourceWithMetadata.source.priority > existing.source.priority) {
        resourceMap[key] = resourceWithMetadata
      }
    }

    return resourceMap.values.sortedByDescending { it.source.priority }.map { it.resource }
  }

  /** Generates a unique key for a resource to identify duplicates. */
  private fun generateResourceKey(resource: Resource): String {
    return try {
      resource.filename ?: resource.uri.toString()
    } catch (e: Exception) {
      resource.description
    }
  }

  /** Gets or creates a PathMatchingResourcePatternResolver for the given type. */
  private fun getResolver(type: String): PathMatchingResourcePatternResolver {
    return resolverPool.computeIfAbsent(type) { PathMatchingResourcePatternResolver(resourceLoader) }
  }

  /** Validates the resource pattern using the ResourceValidator. */
  private fun validatePattern(pattern: String) {
    try {
      ResourceValidator.validatePattern(pattern)
    } catch (e: InvalidResourcePatternException) {
      log.error("Pattern validation failed: {}", e.message)
      throw e
    }
  }

  /**
   * Invalidates cached resources.
   *
   * @param pattern Optional pattern to match for selective invalidation
   */
  fun invalidateCache(pattern: String? = null) {
    cache.invalidate(pattern)
    log.info("Cache invalidated${if (pattern != null) " for pattern: $pattern" else ""}")
  }

  /**
   * Invalidates cached resources for a specific profile.
   *
   * @param profile The profile to invalidate
   */
  fun invalidateCacheForProfile(profile: String) {
    cache.invalidateProfile(profile)
    log.info("Cache invalidated for profile: {}", profile)
  }

  /** Gets cache statistics. */
  fun getCacheStats(): ResourceCache.CacheStats {
    return cache.getStats()
  }

  /** Cleans up expired cache entries. */
  fun cleanupCache() {
    cache.cleanupExpired()
  }

  /** Gets the list of configured resource sources. */
  fun getResourceSources(): List<ResourceSource> {
    return resourceSources.toList()
  }

  /** Internal data class to associate resources with their source metadata. */
  private data class ResourceWithMetadata(val resource: Resource, val source: ResourceSource)
}

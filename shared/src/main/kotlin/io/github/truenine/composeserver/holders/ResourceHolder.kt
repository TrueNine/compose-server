package io.github.truenine.composeserver.holders

import io.github.truenine.composeserver.hasText
import io.github.truenine.composeserver.holders.config.ResourceSource
import io.github.truenine.composeserver.holders.exception.ResourceConfigurationException
import io.github.truenine.composeserver.holders.exception.ResourceResolutionException
import io.github.truenine.composeserver.holders.monitoring.ResourceHolderMetrics
import io.github.truenine.composeserver.holders.resolver.ResourceCache
import io.github.truenine.composeserver.holders.resolver.ResourceResolver
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.properties.DataLoadProperties
import jakarta.annotation.PreDestroy
import org.springframework.boot.system.ApplicationHome
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.util.concurrent.*

/**
 * Enhanced ResourceHolder with Spring Boot-like configuration loading mechanism.
 *
 * This class provides a robust, high-performance resource loading system with:
 * - Property source hierarchy similar to Spring Boot
 * - Environment-specific configuration loading with profiles
 * - Comprehensive error handling and fallback mechanisms
 * - Performance optimizations with caching and resource pooling
 * - Thread-safe operations
 * - Resource change detection (optional)
 * - Detailed logging and debugging support
 *
 * **Resource Resolution Priority (Spring Boot-like):**
 * 1. file:./config/{profile}/{location}/ (highest priority)
 * 2. file:./config/{location}/
 * 3. file:./{location}/
 * 4. classpath:/config/{profile}/{location}/
 * 5. classpath:/config/{location}/
 * 6. classpath:/{location}/ (lowest priority)
 *
 * **Backward Compatibility:** All existing public methods are preserved and work as before, but now benefit from the enhanced architecture and improved error
 * handling.
 *
 * @property home Application home directory
 * @property resourceLoader Spring resource loader
 * @property properties Data loading properties with enhanced configuration
 * @property environment Spring environment for profile detection (optional)
 * @author TrueNine
 * @since 2024-07-18
 */
class ResourceHolder(
  private val home: ApplicationHome,
  private val resourceLoader: ResourceLoader,
  private val properties: DataLoadProperties,
  private val environment: Environment? = null,
) {

  companion object {
    @JvmStatic private val log = logger<ResourceHolder>()
  }

  // Core components
  private val metrics: ResourceHolderMetrics = ResourceHolderMetrics()
  private val cache: ResourceCache? =
    if (properties.enhanced.cache.enabled) {
      log.info("Initializing resource cache (maxSize: {}, ttl: {}ms)", properties.enhanced.cache.maxSize, properties.enhanced.cache.ttlMs)
      ResourceCache(properties.enhanced.cache.maxSize, properties.enhanced.cache.ttlMs)
    } else {
      log.info("Resource caching disabled")
      null
    }
  private val resourceResolver: ResourceResolver = run {
    val resourceSources = createResourceSources()
    log.info("Created {} resource sources", resourceSources.size)
    ResourceResolver(
      resourceSources = resourceSources,
      resourceLoader = resourceLoader,
      cache = cache ?: ResourceCache(1, 1),
      fallbackConfig = properties.enhanced.fallback,
    )
  }
  private val scheduledExecutor: ScheduledExecutorService? =
    if (cache != null && properties.enhanced.cache.enabled) {
      val executor = Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "ResourceHolder-CacheCleanup").apply { isDaemon = true } }
      executor.scheduleAtFixedRate(
        { cache.cleanupExpired() },
        properties.enhanced.cache.cleanupIntervalMs,
        properties.enhanced.cache.cleanupIntervalMs,
        TimeUnit.MILLISECONDS,
      )
      log.info("Scheduled cache cleanup every {}ms", properties.enhanced.cache.cleanupIntervalMs)
      executor
    } else {
      null
    }

  // Legacy properties for backward compatibility
  @Deprecated("Use enhanced configuration instead", ReplaceWith("properties.effectiveConfigLocation"))
  private val prodDir: String
    get() = home.dir.absolutePath

  @Deprecated("Use enhanced configuration instead", ReplaceWith("resourceResolver.resolveResources()"))
  private val prodConfigDir: String
    get() =
      listOf(prodDir, properties.effectiveConfigLocation, properties.effectiveLocation)
        .filter { it.hasText() }
        .joinToString(System.getProperty("file.separator"))

  @Deprecated("Use enhanced configuration instead", ReplaceWith("resourceResolver.resolveResources()"))
  private val internalConfigDir: String
    get() =
      listOf("classpath:${properties.effectiveConfigLocation}", properties.effectiveLocation)
        .filter { it.hasText() }
        .joinToString(System.getProperty("file.separator"))

  init {
    log.info("Initializing enhanced ResourceHolder")
    log.info("Application home: {}", home.dir.absolutePath)
    log.info("Config location: {}", properties.effectiveConfigLocation)
    log.info("Data location: {}", properties.effectiveLocation)
    log.info("Enhanced mode: {}", properties.isEnhancedMode)

    try {
      // Validate configuration if in strict mode
      if (properties.enhanced.strictMode) {
        validateConfiguration()
      }

      log.info("ResourceHolder initialization completed successfully")
      metrics.recordInitializationComplete()
    } catch (e: Exception) {
      log.error("Failed to initialize ResourceHolder", e)
      metrics.recordError(ResourceHolderMetrics.ErrorType.CONFIGURATION)
      throw ResourceConfigurationException("ResourceHolder initialization failed", e)
    }
  }

  /** Creates the resource sources based on configuration. */
  private fun createResourceSources(): List<ResourceSource> {
    val sources = mutableListOf<ResourceSource>()
    val activeProfile = getActiveProfile()

    // Add default Spring Boot-like sources
    sources.addAll(
      ResourceSource.createDefaultSources(
        configLocation = properties.effectiveConfigLocation,
        dataLocation = properties.effectiveLocation,
        applicationHome = home.dir.absolutePath,
        profile = activeProfile,
      )
    )

    // Add custom sources from configuration
    properties.enhanced.customSources.forEach { customSource ->
      try {
        sources.add(customSource.toResourceSource())
        log.debug("Added custom resource source: {}", customSource.description)
      } catch (e: Exception) {
        log.warn("Failed to add custom resource source: {} - {}", customSource.description, e.message)
        if (properties.enhanced.strictMode) {
          throw ResourceConfigurationException("Invalid custom resource source: ${customSource.description}", e)
        }
      }
    }

    return sources.sortedByDescending { it.priority }
  }

  /** Gets the active profile from environment or configuration. */
  private fun getActiveProfile(): String? {
    // Try environment first
    environment?.activeProfiles?.firstOrNull()?.let {
      return it
    }

    // Fall back to configuration
    return properties.enhanced.primaryProfile
  }

  /** Validates the configuration in strict mode. */
  private fun validateConfiguration() {
    log.info("Validating configuration in strict mode")

    // Validate that at least one resource source is accessible
    val testPattern = "**/*"
    val resources = resourceResolver.resolveResources(testPattern)

    if (resources.isEmpty()) {
      log.warn("No resources found with test pattern: {}", testPattern)
    }

    // Validate custom sources
    properties.enhanced.customSources.forEach { customSource ->
      try {
        customSource.toResourceSource()
      } catch (e: Exception) {
        throw ResourceConfigurationException("Invalid custom resource source: ${customSource.description}", e)
      }
    }

    log.info("Configuration validation completed")
  }

  // ========== PUBLIC API METHODS ==========

  /**
   * Gets the first matching resource for the given path.
   *
   * **Backward Compatible Method** - This method maintains the exact same signature and behavior as the original implementation, but now benefits from the
   * enhanced architecture with proper error handling, caching, and precedence logic.
   *
   * @param path The resource path to resolve
   * @return The first matching resource or null if not found
   */
  fun getConfigResource(path: String): Resource? {
    val startTime = System.nanoTime()
    return try {
      val result = resourceResolver.resolveResource(path, getActiveProfile())
      val duration = System.nanoTime() - startTime
      metrics.recordResolution(success = result != null, durationNanos = duration)
      result
    } catch (e: ResourceResolutionException) {
      val duration = System.nanoTime() - startTime
      metrics.recordResolution(success = false, durationNanos = duration)
      metrics.recordError(ResourceHolderMetrics.ErrorType.VALIDATION)
      log.warn("Failed to resolve resource: {} - {}", path, e.message)
      if (properties.enhanced.strictMode) {
        throw e
      }
      null
    }
  }

  /**
   * Gets all matching resources for the given pattern.
   *
   * **Backward Compatible Method** - This method maintains the exact same signature and behavior as the original implementation, but now benefits from the
   * enhanced architecture with proper error handling, caching, and precedence logic.
   *
   * @param pattern The resource pattern to match
   * @return List of matching resources
   */
  fun matchConfigResources(pattern: String): List<Resource> {
    return try {
      resourceResolver.resolveResources(pattern, getActiveProfile())
    } catch (e: ResourceResolutionException) {
      log.warn("Failed to resolve resources for pattern: {} - {}", pattern, e.message)
      if (properties.enhanced.strictMode) {
        throw e
      }
      emptyList()
    }
  }

  // ========== ENHANCED API METHODS ==========

  /**
   * Gets the first matching resource for the given path and profile.
   *
   * **Enhanced Method** - Provides explicit profile support for environment-specific resource resolution.
   *
   * @param path The resource path to resolve
   * @param profile The profile for environment-specific resolution
   * @return The first matching resource or null if not found
   */
  fun getConfigResource(path: String, profile: String?): Resource? {
    return try {
      resourceResolver.resolveResource(path, profile)
    } catch (e: ResourceResolutionException) {
      log.warn("Failed to resolve resource: {} (profile: {}) - {}", path, profile, e.message)
      if (properties.enhanced.strictMode) {
        throw e
      }
      null
    }
  }

  /**
   * Gets all matching resources for the given pattern and profile.
   *
   * **Enhanced Method** - Provides explicit profile support for environment-specific resource resolution.
   *
   * @param pattern The resource pattern to match
   * @param profile The profile for environment-specific resolution
   * @return List of matching resources in priority order
   */
  fun matchConfigResources(pattern: String, profile: String?): List<Resource> {
    return try {
      resourceResolver.resolveResources(pattern, profile)
    } catch (e: ResourceResolutionException) {
      log.warn("Failed to resolve resources for pattern: {} (profile: {}) - {}", pattern, profile, e.message)
      if (properties.enhanced.strictMode) {
        throw e
      }
      emptyList()
    }
  }

  /**
   * Gets all configured resource sources.
   *
   * @return List of resource sources in priority order
   */
  fun getResourceSources(): List<ResourceSource> {
    return resourceResolver.getResourceSources()
  }

  /**
   * Invalidates cached resources.
   *
   * @param pattern Optional pattern to match for selective invalidation
   */
  fun invalidateCache(pattern: String? = null) {
    resourceResolver.invalidateCache(pattern)
    log.info("Cache invalidated${if (pattern != null) " for pattern: $pattern" else ""}")
  }

  /**
   * Invalidates cached resources for a specific profile.
   *
   * @param profile The profile to invalidate
   */
  fun invalidateCacheForProfile(profile: String) {
    resourceResolver.invalidateCacheForProfile(profile)
    log.info("Cache invalidated for profile: {}", profile)
  }

  /**
   * Gets cache statistics.
   *
   * @return Cache statistics or null if caching is disabled
   */
  fun getCacheStats(): ResourceCache.CacheStats? {
    return cache?.getStats()
  }

  /**
   * Checks if enhanced mode is enabled.
   *
   * @return true if enhanced features are active
   */
  fun isEnhancedMode(): Boolean {
    return properties.isEnhancedMode
  }

  /**
   * Gets the active profiles.
   *
   * @return List of active profiles
   */
  fun getActiveProfiles(): List<String> {
    val profiles = mutableListOf<String>()

    // Add environment profiles
    environment?.activeProfiles?.let { profiles.addAll(it) }

    // Add configured profiles
    profiles.addAll(properties.enhanced.activeProfiles)

    return profiles.distinct()
  }

  /**
   * Gets performance metrics for monitoring and debugging.
   *
   * @return Performance statistics
   */
  fun getPerformanceMetrics(): ResourceHolderMetrics.PerformanceStats {
    // Update cache metrics if available
    cache?.getStats()?.let { cacheStats -> metrics.updateCacheMetrics(cacheStats.hitCount, cacheStats.missCount, cacheStats.evictionCount) }

    return metrics.getPerformanceStats()
  }

  /**
   * Gets a human-readable performance summary.
   *
   * @return Performance summary string
   */
  fun getPerformanceSummary(): String {
    return metrics.getPerformanceSummary()
  }

  /** Cleanup method called on bean destruction. */
  @PreDestroy
  fun destroy() {
    log.info("Shutting down ResourceHolder")

    scheduledExecutor?.let { executor ->
      executor.shutdown()
      try {
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
          executor.shutdownNow()
        }
      } catch (e: InterruptedException) {
        executor.shutdownNow()
        Thread.currentThread().interrupt()
      }
    }

    log.info("ResourceHolder shutdown completed")
  }
}

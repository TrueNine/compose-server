package io.github.truenine.composeserver.properties

import io.github.truenine.composeserver.holders.config.ResourceHolderConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.shared"

/**
 * Data loading properties with enhanced configuration support.
 *
 * This class maintains backward compatibility with the original DataLoadProperties while providing access to enhanced ResourceHolder configuration features.
 *
 * @property location The data location directory (default: "data")
 * @property matchFiles List of file patterns to match
 * @property configLocation The configuration location directory (default: "config")
 * @property enhanced Enhanced configuration for advanced features
 * @author TrueNine
 * @since 2024-07-18
 */
@ConfigurationProperties(prefix = "$PREFIX.data-load")
data class DataLoadProperties(
  var location: String = "data",
  var matchFiles: MutableList<String> = mutableListOf(),
  var configLocation: String = "config",

  /** Enhanced configuration properties for advanced ResourceHolder features. This provides access to profile-based resolution, caching, custom sources, etc. */
  @NestedConfigurationProperty
  var enhanced: ResourceHolderConfiguration = ResourceHolderConfiguration(configLocation = configLocation, location = location, matchFiles = matchFiles),
) {

  /** Gets the effective configuration location, preferring enhanced config if available. */
  val effectiveConfigLocation: String
    get() = if (enhanced.configLocation != "config") enhanced.configLocation else configLocation

  /** Gets the effective data location, preferring enhanced config if available. */
  val effectiveLocation: String
    get() = if (enhanced.location != "data") enhanced.location else location

  /** Gets the effective match files, merging both configurations. */
  val effectiveMatchFiles: List<String>
    get() {
      val combined = mutableSetOf<String>()
      combined.addAll(matchFiles)
      combined.addAll(enhanced.matchFiles)
      return combined.toList()
    }

  /** Checks if enhanced features are enabled (has non-default configuration). */
  val isEnhancedMode: Boolean
    get() =
      enhanced.activeProfiles.isNotEmpty() ||
        enhanced.customSources.isNotEmpty() ||
        !enhanced.cache.enabled ||
        enhanced.strictMode ||
        enhanced.enableChangeDetection
}

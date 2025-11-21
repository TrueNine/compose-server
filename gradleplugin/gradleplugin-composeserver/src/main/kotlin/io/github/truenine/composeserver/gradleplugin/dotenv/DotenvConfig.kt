package io.github.truenine.composeserver.gradleplugin.dotenv

/**
 * # Dotenv environment variable configuration
 *
 * Used to configure options for loading environment variables from .env files.
 *
 * @author TrueNine
 * @since 2024-12-19
 */
open class DotenvConfig {

  /** Whether to enable dotenv environment variable loading */
  var enabled: Boolean = false

  /** dotenv file path, supports absolute and relative paths (relative to project root) */
  var filePath: String = ""

  /** Whether to log a warning when the file does not exist */
  var warnOnMissingFile: Boolean = true

  /** Whether to show detailed error information when parsing fails */
  var verboseErrors: Boolean = true

  /** Whether to override existing environment variables */
  var overrideExisting: Boolean = false

  /** Whether to ignore empty values */
  var ignoreEmptyValues: Boolean = false

  /** Environment variable name prefix filter; only variables with the prefix will be loaded */
  var prefixFilter: String? = null

  /** Names of environment variables to exclude */
  var excludeKeys: MutableSet<String> = mutableSetOf()

  /** Names of environment variables to include; if set, only these variables will be loaded */
  var includeKeys: MutableSet<String> = mutableSetOf()

  /**
   * Set the dotenv file path
   *
   * @param path file path; supports absolute and relative paths
   */
  fun filePath(path: String) {
    filePath = path
  }

  /**
   * Configure whether to show a warning if the dotenv file does not exist
   *
   * @param warn whether to show a warning
   */
  fun warnOnMissingFile(warn: Boolean) {
    warnOnMissingFile = warn
  }

  /**
   * Configure whether to show detailed error messages
   *
   * @param verbose whether to show detailed information
   */
  fun verboseErrors(verbose: Boolean) {
    verboseErrors = verbose
  }

  /**
   * Configure whether to override existing environment variables
   *
   * @param override whether to override
   */
  fun overrideExisting(override: Boolean) {
    overrideExisting = override
  }

  /**
   * Configure whether to ignore empty values
   *
   * @param ignore whether to ignore
   */
  fun ignoreEmptyValues(ignore: Boolean) {
    ignoreEmptyValues = ignore
  }

  /**
   * Set environment variable name prefix filter
   *
   * @param prefix prefix string
   */
  fun prefixFilter(prefix: String?) {
    prefixFilter = prefix
  }

  /**
   * Add environment variable names to be excluded
   *
   * @param keys variable names to exclude
   */
  fun excludeKeys(vararg keys: String) {
    excludeKeys.addAll(keys)
  }

  /**
   * Set the list of environment variable names to include
   *
   * @param keys variable names to include
   */
  fun includeKeys(vararg keys: String) {
    includeKeys.addAll(keys)
  }

  /** Clear the exclude list */
  fun clearExcludeKeys() {
    excludeKeys.clear()
  }

  /** Clear the include list */
  fun clearIncludeKeys() {
    includeKeys.clear()
  }

  /**
   * Check whether the configuration is valid
   *
   * @return whether the configuration is valid
   */
  fun isValid(): Boolean {
    return enabled && filePath.isNotBlank()
  }

  /**
   * Get configuration summary information
   *
   * @return configuration summary string
   */
  fun getSummary(): String {
    return buildString {
      append("DotenvConfig(")
      append("enabled=$enabled, ")
      append("filePath='$filePath', ")
      append("warnOnMissingFile=$warnOnMissingFile, ")
      append("verboseErrors=$verboseErrors, ")
      append("overrideExisting=$overrideExisting, ")
      append("ignoreEmptyValues=$ignoreEmptyValues")
      prefixFilter?.let { append(", prefixFilter='$it'") }
      if (excludeKeys.isNotEmpty()) append(", excludeKeys=$excludeKeys")
      if (includeKeys.isNotEmpty()) append(", includeKeys=$includeKeys")
      append(")")
    }
  }
}

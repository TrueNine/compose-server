package io.github.truenine.composeserver.holders.validation

import io.github.truenine.composeserver.hasText
import io.github.truenine.composeserver.holders.config.ResourceSource
import io.github.truenine.composeserver.holders.exception.InvalidResourcePatternException
import io.github.truenine.composeserver.holders.exception.InvalidResourceSourceException
import io.github.truenine.composeserver.logger
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Validator for resource patterns and sources to ensure security and correctness.
 *
 * This validator provides comprehensive validation for:
 * - Resource patterns (security checks, syntax validation)
 * - Resource sources (accessibility, configuration validation)
 * - Path traversal prevention
 * - Malicious pattern detection
 *
 * @author TrueNine
 * @since 2024-07-18
 */
object ResourceValidator {

  private val log = logger<ResourceValidator>()

  // Security patterns to prevent path traversal and other attacks
  private val FORBIDDEN_PATTERNS =
    listOf(
      ".*\\.\\./.*", // Path traversal
      ".*\\.\\\\.*", // Windows path traversal
      ".*\\.\\.\\\\.*", // Mixed path traversal
      ".*/etc/passwd.*", // Unix system files
      ".*/etc/shadow.*", // Unix system files
      ".*/proc/.*", // Unix proc filesystem
      ".*\\\\windows\\\\.*", // Windows system directories
      ".*file:///etc/.*", // Absolute file URLs to system dirs
      ".*jar:file:.*", // JAR file access
      ".*\\$\\{.*\\}.*", // Variable expansion attacks
    )

  private val ALLOWED_PROTOCOLS = setOf("classpath", "file", "http", "https")

  /**
   * Validates a resource pattern for security and syntax correctness.
   *
   * @param pattern The resource pattern to validate
   * @throws InvalidResourcePatternException if the pattern is invalid or unsafe
   */
  fun validatePattern(pattern: String) {
    if (!pattern.hasText()) {
      throw InvalidResourcePatternException("Resource pattern cannot be empty")
    }

    // Check for forbidden patterns
    FORBIDDEN_PATTERNS.forEach { forbiddenPattern ->
      if (Pattern.matches(forbiddenPattern, pattern)) {
        log.warn("Blocked potentially dangerous resource pattern: {}", pattern)
        throw InvalidResourcePatternException("Pattern contains forbidden sequence: $pattern")
      }
    }

    // Check for excessively long patterns (potential DoS)
    if (pattern.length > 1000) {
      throw InvalidResourcePatternException("Pattern too long (max 1000 characters): ${pattern.length}")
    }

    // Validate regex patterns if they contain regex metacharacters
    if (containsRegexMetacharacters(pattern)) {
      try {
        Pattern.compile(pattern)
      } catch (e: PatternSyntaxException) {
        throw InvalidResourcePatternException("Invalid regex pattern: $pattern", e)
      }
    }

    // Check for null bytes (potential security issue)
    if (pattern.contains('\u0000')) {
      throw InvalidResourcePatternException("Pattern contains null bytes")
    }

    log.debug("Pattern validation passed: {}", pattern)
  }

  /**
   * Validates a resource source configuration.
   *
   * @param source The resource source to validate
   * @throws InvalidResourceSourceException if the source is invalid
   */
  fun validateResourceSource(source: ResourceSource) {
    // Validate path
    if (!source.path.hasText()) {
      throw InvalidResourceSourceException("Resource source path cannot be empty")
    }

    // Check for forbidden patterns in path
    FORBIDDEN_PATTERNS.forEach { forbiddenPattern ->
      if (Pattern.matches(forbiddenPattern, source.path)) {
        log.warn("Blocked potentially dangerous resource source path: {}", source.path)
        throw InvalidResourceSourceException("Source path contains forbidden sequence: ${source.path}")
      }
    }

    // Validate protocol for URL sources
    if (source.type.name.equals("URL", ignoreCase = true)) {
      val protocol = extractProtocol(source.path)
      if (protocol != null && !ALLOWED_PROTOCOLS.contains(protocol.lowercase())) {
        throw InvalidResourceSourceException("Unsupported protocol: $protocol in ${source.path}")
      }
    }

    // Validate priority
    if (source.priority < 0) {
      throw InvalidResourceSourceException("Resource source priority must be non-negative: ${source.priority}")
    }

    // Validate profile name if present
    source.profile?.let { profile ->
      if (!isValidProfileName(profile)) {
        throw InvalidResourceSourceException("Invalid profile name: $profile")
      }
    }

    log.debug("Resource source validation passed: {}", source.description)
  }

  /**
   * Validates multiple resource sources and checks for conflicts.
   *
   * @param sources List of resource sources to validate
   * @throws InvalidResourceSourceException if any source is invalid or conflicts exist
   */
  fun validateResourceSources(sources: List<ResourceSource>) {
    if (sources.isEmpty()) {
      log.warn("No resource sources configured")
      return
    }

    // Validate each source individually
    sources.forEach { validateResourceSource(it) }

    // Check for duplicate priorities within the same profile
    val priorityGroups = sources.groupBy { it.profile }
    priorityGroups.forEach { (profile, sourcesForProfile) ->
      val priorities = sourcesForProfile.map { it.priority }
      val duplicatePriorities = priorities.groupBy { it }.filter { it.value.size > 1 }.keys

      if (duplicatePriorities.isNotEmpty()) {
        log.warn("Duplicate priorities found for profile '{}': {}", profile, duplicatePriorities)
        // This is a warning, not an error, as Spring Boot allows this
      }
    }

    // Check for reasonable number of sources (performance consideration)
    if (sources.size > 50) {
      log.warn("Large number of resource sources configured ({}), this may impact performance", sources.size)
    }

    log.info("Validated {} resource sources successfully", sources.size)
  }

  /** Checks if a pattern contains regex metacharacters but not common path patterns. */
  private fun containsRegexMetacharacters(pattern: String): Boolean {
    // Common path patterns should not be treated as regex
    if (pattern.matches(Regex(".*\\*\\*/.*")) || pattern.matches(Regex(".*/\\*.*")) || pattern.endsWith("*") || pattern.startsWith("*")) {
      return false // This is likely an Ant-style path pattern
    }

    // Check for regex metacharacters that suggest regex usage (excluding '.' which is common in file extensions)
    val regexMetaChars = setOf('^', '$', '+', '?', '{', '}', '[', ']', '(', ')', '|', '\\')
    return pattern.any { it in regexMetaChars }
  }

  /** Extracts the protocol from a URL string. */
  private fun extractProtocol(url: String): String? {
    val colonIndex = url.indexOf(':')
    return if (colonIndex > 0) {
      url.substring(0, colonIndex)
    } else {
      null
    }
  }

  /** Validates a profile name according to Spring Boot conventions. */
  private fun isValidProfileName(profile: String): Boolean {
    if (!profile.hasText()) return false

    // Profile names should be alphanumeric with hyphens and underscores
    val validProfilePattern = "^[a-zA-Z0-9_-]+$"
    return Pattern.matches(validProfilePattern, profile) && profile.length <= 50
  }

  /**
   * Sanitizes a resource pattern by removing potentially dangerous elements.
   *
   * This method should be used with caution as it may alter the intended pattern.
   *
   * @param pattern The pattern to sanitize
   * @return Sanitized pattern
   */
  fun sanitizePattern(pattern: String): String {
    var sanitized = pattern

    // Remove null bytes
    sanitized = sanitized.replace('\u0000', ' ')

    // Remove obvious path traversal attempts
    sanitized = sanitized.replace("../", "")
    sanitized = sanitized.replace("..\\", "")

    // Limit length
    if (sanitized.length > 1000) {
      sanitized = sanitized.substring(0, 1000)
      log.warn("Truncated overly long pattern to 1000 characters")
    }

    return sanitized.trim()
  }

  /**
   * Checks if a pattern is safe to use without throwing exceptions.
   *
   * @param pattern The pattern to check
   * @return true if the pattern is safe, false otherwise
   */
  fun isPatternSafe(pattern: String): Boolean {
    return try {
      validatePattern(pattern)
      true
    } catch (e: InvalidResourcePatternException) {
      false
    }
  }

  /**
   * Checks if a resource source is valid without throwing exceptions.
   *
   * @param source The source to check
   * @return true if the source is valid, false otherwise
   */
  fun isResourceSourceValid(source: ResourceSource): Boolean {
    return try {
      validateResourceSource(source)
      true
    } catch (e: InvalidResourceSourceException) {
      false
    }
  }
}

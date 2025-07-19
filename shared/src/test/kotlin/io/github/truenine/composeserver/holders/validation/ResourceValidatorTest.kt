package io.github.truenine.composeserver.holders.validation

import io.github.truenine.composeserver.holders.config.ResourceSource
import io.github.truenine.composeserver.holders.config.ResourceType
import io.github.truenine.composeserver.holders.exception.InvalidResourcePatternException
import io.github.truenine.composeserver.holders.exception.InvalidResourceSourceException
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ResourceValidatorTest {

  @Nested
  inner class PatternValidationTest {

    @Test
    fun `should validate safe patterns`() {
      val safePatterns = listOf("config.yml", "application.properties", "data/*.yml", "**/config/**", "classpath:config/data.yml", "file:./config/app.yml")

      safePatterns.forEach { pattern ->
        try {
          ResourceValidator.validatePattern(pattern)
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Pattern '$pattern' should be valid but threw: $e")
        }
      }
    }

    @Test
    fun `should reject dangerous path traversal patterns`() {
      val dangerousPatterns =
        listOf(
          "../../../etc/passwd",
          "..\\..\\windows\\system32",
          "./../../config/../../../etc/passwd",
          "config/../../../etc/shadow",
          "data\\..\\..\\windows\\system.ini",
        )

      dangerousPatterns.forEach { pattern ->
        assertThrows<InvalidResourcePatternException>("Pattern '$pattern' should be rejected") { ResourceValidator.validatePattern(pattern) }
      }
    }

    @Test
    fun `should reject system file access patterns`() {
      val systemPatterns = listOf("/etc/passwd", "file:///etc/shadow", "/proc/version", "\\windows\\system32\\config", "jar:file:///opt/app.jar!/config")

      systemPatterns.forEach { pattern ->
        assertThrows<InvalidResourcePatternException>("Pattern '$pattern' should be rejected") { ResourceValidator.validatePattern(pattern) }
      }
    }

    @Test
    fun `should reject variable expansion patterns`() {
      val expansionPatterns = listOf("\${java.home}/config.yml", "config/\${user.name}.properties", "\${env:HOME}/.config/app.yml")

      expansionPatterns.forEach { pattern ->
        assertThrows<InvalidResourcePatternException>("Pattern '$pattern' should be rejected") { ResourceValidator.validatePattern(pattern) }
      }
    }

    @Test
    fun `should reject empty patterns`() {
      assertThrows<InvalidResourcePatternException> { ResourceValidator.validatePattern("") }

      assertThrows<InvalidResourcePatternException> { ResourceValidator.validatePattern("   ") }
    }

    @Test
    fun `should reject patterns that are too long`() {
      val longPattern = "a".repeat(1001)

      assertThrows<InvalidResourcePatternException> { ResourceValidator.validatePattern(longPattern) }
    }

    @Test
    fun `should reject patterns with null bytes`() {
      val patternWithNullByte = "config\u0000.yml"

      assertThrows<InvalidResourcePatternException> { ResourceValidator.validatePattern(patternWithNullByte) }
    }

    @Test
    fun `should validate regex patterns correctly`() {
      val validRegexPatterns = listOf("config\\.(yml|yaml|properties)", ".*\\.yml$", "^application-.*\\.properties$")

      validRegexPatterns.forEach { pattern ->
        try {
          ResourceValidator.validatePattern(pattern)
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Regex pattern '$pattern' should be valid but threw: $e")
        }
      }
    }

    @Test
    fun `should reject invalid regex patterns`() {
      val invalidRegexPatterns =
        listOf(
          "config[.yml", // Unclosed bracket
          "config\\", // Trailing backslash
          "config(?invalidgroup)", // Invalid group
        )

      invalidRegexPatterns.forEach { pattern ->
        assertThrows<InvalidResourcePatternException>("Invalid regex pattern '$pattern' should be rejected") { ResourceValidator.validatePattern(pattern) }
      }
    }
  }

  @Nested
  inner class ResourceSourceValidationTest {

    @Test
    fun `should validate filesystem resource sources`() {
      val validSources =
        listOf(
          ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000),
          ResourceSource(ResourceType.FILESYSTEM, "./config", 500),
          ResourceSource(ResourceType.FILESYSTEM, "relative/path", 100, "dev"),
        )

      validSources.forEach { source ->
        try {
          ResourceValidator.validateResourceSource(source)
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Source should be valid: ${source.path} but threw: $e")
        }
      }
    }

    @Test
    fun `should validate classpath resource sources`() {
      val validSources =
        listOf(
          ResourceSource(ResourceType.CLASSPATH, "config", 1000),
          ResourceSource(ResourceType.CLASSPATH, "config/data", 500),
          ResourceSource(ResourceType.CLASSPATH, "META-INF/config", 100, "prod"),
        )

      validSources.forEach { source ->
        try {
          ResourceValidator.validateResourceSource(source)
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Source should be valid: ${source.path} but threw: $e")
        }
      }
    }

    @Test
    fun `should validate URL resource sources with allowed protocols`() {
      val validSources =
        listOf(
          ResourceSource(ResourceType.URL, "http://config.example.com/config.yml", 1000),
          ResourceSource(ResourceType.URL, "https://secure.example.com/config", 500),
          ResourceSource(ResourceType.URL, "file:///opt/config", 100),
          ResourceSource(ResourceType.URL, "classpath:config/data.yml", 50),
        )

      validSources.forEach { source ->
        try {
          ResourceValidator.validateResourceSource(source)
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Source should be valid: ${source.path} but threw: $e")
        }
      }
    }

    @Test
    fun `should reject URL sources with disallowed protocols`() {
      val invalidSources =
        listOf(
          ResourceSource(ResourceType.URL, "ftp://ftp.example.com/config.yml", 1000),
          ResourceSource(ResourceType.URL, "ldap://ldap.example.com/config", 500),
          ResourceSource(ResourceType.URL, "jar:file:///app.jar!/config", 100),
        )

      invalidSources.forEach { source ->
        assertThrows<InvalidResourceSourceException>("Source should be rejected: ${source.path}") { ResourceValidator.validateResourceSource(source) }
      }
    }

    @Test
    fun `should reject sources with empty paths`() {
      assertThrows<InvalidResourceSourceException> { ResourceValidator.validateResourceSource(ResourceSource(ResourceType.FILESYSTEM, "", 1000)) }

      assertThrows<InvalidResourceSourceException> { ResourceValidator.validateResourceSource(ResourceSource(ResourceType.CLASSPATH, "   ", 500)) }
    }

    @Test
    fun `should reject sources with negative priority`() {
      assertThrows<InvalidResourceSourceException> { ResourceValidator.validateResourceSource(ResourceSource(ResourceType.FILESYSTEM, "/opt/config", -1)) }
    }

    @Test
    fun `should reject sources with dangerous paths`() {
      val dangerousSources =
        listOf(
          ResourceSource(ResourceType.FILESYSTEM, "../../../etc", 1000),
          ResourceSource(ResourceType.CLASSPATH, "../../config", 500),
          ResourceSource(ResourceType.URL, "file:///../../../etc/passwd", 100),
        )

      dangerousSources.forEach { source ->
        assertThrows<InvalidResourceSourceException>("Dangerous source should be rejected: ${source.path}") { ResourceValidator.validateResourceSource(source) }
      }
    }

    @Test
    fun `should validate profile names`() {
      val validProfiles = listOf("dev", "prod", "test", "staging", "dev-local", "prod_1")
      val invalidProfiles = listOf("", " ", "dev with spaces", "dev@prod", "very-long-profile-name-that-exceeds-fifty-characters-limit")

      validProfiles.forEach { profile ->
        try {
          ResourceValidator.validateResourceSource(ResourceSource(ResourceType.FILESYSTEM, "/config", 1000, profile))
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Profile '$profile' should be valid but threw: $e")
        }
      }

      invalidProfiles.forEach { profile ->
        assertThrows<InvalidResourceSourceException>("Profile '$profile' should be rejected") {
          ResourceValidator.validateResourceSource(ResourceSource(ResourceType.FILESYSTEM, "/config", 1000, profile))
        }
      }
    }
  }

  @Nested
  inner class MultipleSourceValidationTest {

    @Test
    fun `should validate multiple valid sources`() {
      val sources =
        listOf(
          ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000, "prod"),
          ResourceSource(ResourceType.CLASSPATH, "config", 500, "dev"),
          ResourceSource(ResourceType.URL, "https://config.example.com", 100),
        )

      try {
        ResourceValidator.validateResourceSources(sources)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Valid sources should not throw exception: $e")
      }
    }

    @Test
    fun `should handle empty source list`() {
      try {
        ResourceValidator.validateResourceSources(emptyList())
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Empty source list should not throw exception: $e")
      }
    }

    @Test
    fun `should warn about duplicate priorities but not fail`() {
      val sources =
        listOf(
          ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000, "dev"),
          ResourceSource(ResourceType.CLASSPATH, "config", 1000, "dev"), // Same priority
          ResourceSource(ResourceType.URL, "https://config.example.com", 500),
        )

      // Should not throw exception, just log warning
      try {
        ResourceValidator.validateResourceSources(sources)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Duplicate priorities should not cause failure: $e")
      }
    }

    @Test
    fun `should warn about large number of sources`() {
      val sources = (1..60).map { i -> ResourceSource(ResourceType.FILESYSTEM, "/config$i", i * 10) }

      // Should not throw exception, just log warning
      try {
        ResourceValidator.validateResourceSources(sources)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Large number of sources should not cause failure: $e")
      }
    }

    @Test
    fun `should fail if any individual source is invalid`() {
      val sources =
        listOf(
          ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000),
          ResourceSource(ResourceType.CLASSPATH, "../../../etc", 500), // Invalid dangerous path
          ResourceSource(ResourceType.URL, "https://config.example.com", 100),
        )

      assertThrows<InvalidResourceSourceException> { ResourceValidator.validateResourceSources(sources) }
    }
  }

  @Nested
  inner class UtilityMethodTest {

    @Test
    fun `should check pattern safety without throwing`() {
      assertTrue(ResourceValidator.isPatternSafe("config.yml"))
      assertTrue(ResourceValidator.isPatternSafe("data/*.properties"))
      assertFalse(ResourceValidator.isPatternSafe("../../../etc/passwd"))
      assertFalse(ResourceValidator.isPatternSafe(""))
      assertFalse(ResourceValidator.isPatternSafe("config\u0000.yml"))
    }

    @Test
    fun `should check resource source validity without throwing`() {
      val validSource = ResourceSource(ResourceType.FILESYSTEM, "/opt/config", 1000)
      assertTrue(ResourceValidator.isResourceSourceValid(validSource))

      // For invalid sources that can't be constructed, test the validation method directly
      // by creating sources with dangerous patterns that pass construction but fail validation
      val dangerousSource = ResourceSource(ResourceType.FILESYSTEM, "../../../etc", 1000)
      assertFalse(ResourceValidator.isResourceSourceValid(dangerousSource))
    }

    @Test
    fun `should sanitize dangerous patterns`() {
      assertEquals("config.yml", ResourceValidator.sanitizePattern("config.yml"))
      assertEquals("config.yml", ResourceValidator.sanitizePattern("../config.yml"))
      assertEquals("config.yml", ResourceValidator.sanitizePattern("..\\config.yml"))
      assertEquals("config yml", ResourceValidator.sanitizePattern("config\u0000yml"))

      val longPattern = "a".repeat(1500)
      val sanitized = ResourceValidator.sanitizePattern(longPattern)
      assertEquals(1000, sanitized.length)
    }

    @Test
    fun `should handle null or blank input in sanitization`() {
      assertEquals("", ResourceValidator.sanitizePattern(""))
      assertEquals("", ResourceValidator.sanitizePattern("   "))
      assertEquals("config", ResourceValidator.sanitizePattern("  config  "))
    }
  }

  @Nested
  inner class EdgeCaseTest {

    @Test
    fun `should handle Unicode characters in patterns`() {
      val unicodePattern = "配置/应用.yml"
      try {
        ResourceValidator.validatePattern(unicodePattern)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Unicode pattern should be valid: $e")
      }
    }

    @Test
    fun `should handle special characters in valid patterns`() {
      val specialPatterns = listOf("config-file.yml", "config_file.properties", "config.file.yaml", "config@2024.yml")

      specialPatterns.forEach { pattern ->
        try {
          ResourceValidator.validatePattern(pattern)
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("Pattern '$pattern' should be valid but threw: $e")
        }
      }
    }

    @Test
    fun `should handle URL edge cases`() {
      val edgeCaseUrls =
        listOf(
          "http://localhost:8080/config.yml",
          "https://config.example.com:443/path/to/config",
          "file:///C:/Windows/config.yml", // Windows path
        )

      edgeCaseUrls.forEach { url ->
        try {
          ResourceValidator.validateResourceSource(ResourceSource(ResourceType.URL, url, 1000))
          // Test passes if no exception is thrown
        } catch (e: Exception) {
          fail("URL '$url' should be valid but threw: $e")
        }
      }
    }

    @Test
    fun `should handle borderline pattern lengths`() {
      val maxLengthPattern = "a".repeat(1000)
      try {
        ResourceValidator.validatePattern(maxLengthPattern)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Max length pattern should be valid: $e")
      }

      val tooLongPattern = "a".repeat(1001)
      assertThrows<InvalidResourcePatternException> { ResourceValidator.validatePattern(tooLongPattern) }
    }

    @Test
    fun `should handle complex regex patterns`() {
      val complexRegexPattern = "^(application|config)(-[a-z0-9]+)?\\.(yml|yaml|properties)$"
      try {
        ResourceValidator.validatePattern(complexRegexPattern)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Complex regex pattern should be valid: $e")
      }
    }

    @Test
    fun `should handle mixed path separators`() {
      val mixedSeparators = "config\\data/files\\*.yml"
      // Should not fail validation as we normalize separators
      try {
        ResourceValidator.validatePattern(mixedSeparators)
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Mixed separators pattern should be valid: $e")
      }
    }
  }
}

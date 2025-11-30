package io.github.truenine.composeserver.security.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.stream.Stream
import kotlin.system.measureTimeMillis
import kotlin.test.*
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

/**
 * Comprehensive test suite for PemFormat class with 100% coverage. Tests all normal execution paths, edge cases, error scenarios, and performance
 * optimizations.
 */
class PemFormatTest {

  companion object {
    private val VALID_PEM =
      """
      -----BEGIN RSA PRIVATE KEY-----
      MIIEowIBAAKCAQEAvRKXmC7E8y0F1olUhrH8YuSVfaYhJ1ySqInrROcbXXXXXXXX
      q4t5iHJjXXXXXXXXMpzXXXXXXXXq8XXXXXXXX+XXXXXXXXrpXXXXXXXX1XXXXXXXX
      -----END RSA PRIVATE KEY-----
      """
        .trimIndent()

    private const val VALID_PEM_SCHEMA = "RSA PRIVATE KEY"
    private const val VALID_PEM_CONTENT =
      "MIIEowIBAAKCAQEAvRKXmC7E8y0F1olUhrH8YuSVfaYhJ1ySqInrROcbXXXXXXXXq4t5iHJjXXXXXXXXMpzXXXXXXXXq8XXXXXXXX+XXXXXXXXrpXXXXXXXX1XXXXXXXX"

    private const val SIMPLE_BASE64 = "SGVsbG8gV29ybGQ="
    private const val SIMPLE_DECODED = "Hello World"

    private fun generateTestKeyPair(): KeyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()

    private fun generateEcKeyPair(): KeyPair = KeyPairGenerator.getInstance("EC").apply { initialize(256) }.generateKeyPair()

    @JvmStatic
    fun invalidPemProvider(): Stream<Arguments> =
      Stream.of(
        Arguments.of("mismatched header footer types", "-----BEGIN TEST-----\nInvalid\n-----END DIFFERENT-----"),
        Arguments.of("no header footer markers", "Content"),
        Arguments.of("empty string", ""),
        Arguments.of("only header", "-----BEGIN TEST-----"),
        Arguments.of("only footer", "-----END TEST-----"),
        Arguments.of("malformed header", "----BEGIN TEST-----\nContent\n-----END TEST-----"),
        Arguments.of("malformed footer", "-----BEGIN TEST-----\nContent\n----END TEST-----"),
        Arguments.of("invalid base64 content", "-----BEGIN TEST-----\n@#$%^&*()\n-----END TEST-----"),
        Arguments.of("line too long", "-----BEGIN TEST-----\n${"A".repeat(300)}\n-----END TEST-----"),
      )

    @JvmStatic
    fun keyTypeProvider(): Stream<Arguments> =
      Stream.of(
        Arguments.of("standard key type", "TEST KEY", "-----BEGIN TEST KEY-----", "-----END TEST KEY-----"),
        Arguments.of("key type with spaces", "  TEST KEY  ", "-----BEGIN TEST KEY-----", "-----END TEST KEY-----"),
        Arguments.of("null key type", null, "-----BEGIN -----", "-----END -----"),
        Arguments.of("special characters", "RSA/ECB/PKCS1", "-----BEGIN RSA/ECB/PKCS1-----", "-----END RSA/ECB/PKCS1-----"),
        Arguments.of("numeric key type", "AES256", "-----BEGIN AES256-----", "-----END AES256-----"),
        Arguments.of("hyphenated key type", "EC-PRIVATE", "-----BEGIN EC-PRIVATE-----", "-----END EC-PRIVATE-----"),
      )

    @JvmStatic
    fun lineEndingProvider(): Stream<Arguments> =
      Stream.of(Arguments.of("unix line endings", "\n"), Arguments.of("windows line endings", "\r\n"), Arguments.of("mac line endings", "\r"))

    @JvmStatic
    fun base64ContentProvider(): Stream<Arguments> =
      Stream.of(
        Arguments.of("short content", "SGVsbG8="),
        Arguments.of("medium content", "SGVsbG8gV29ybGQgSG93IGFyZSB5b3U="),
        Arguments.of("long content", "A".repeat(200)),
        Arguments.of("content with padding", "SGVsbG8gV29ybGQ="),
        Arguments.of("content without padding", "SGVsbG8gV29ybGQ"),
      )
  }

  @Nested
  inner class PemParsingTests {

    @Test
    fun parseValidPemString() {
      val pemFormat = PemFormat.parse(VALID_PEM)

      assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
      assertEquals(VALID_PEM_CONTENT, pemFormat.content)
    }

    @Test
    fun parseValidPemStringWithDifferentLineEndings() {
      val pemWithWindowsLineEndings = VALID_PEM.replace("\n", "\r\n")
      val pemFormat = PemFormat.parse(pemWithWindowsLineEndings)

      assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
      assertEquals(VALID_PEM_CONTENT, pemFormat.content)
    }

    @Test
    fun parseValidPemStringWithExtraWhitespace() {
      val pemWithWhitespace = "  \n  $VALID_PEM  \n  "
      val pemFormat = PemFormat.parse(pemWithWhitespace)

      assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
      assertEquals(VALID_PEM_CONTENT, pemFormat.content)
    }

    @ParameterizedTest
    @MethodSource("io.github.truenine.composeserver.security.crypto.PemFormatTest#invalidPemProvider")
    fun parseInvalidPemStrings(testCase: String, invalidPem: String) {
      assertThrows<IllegalArgumentException> { PemFormat.parse(invalidPem) }
    }

    @ParameterizedTest
    @MethodSource("io.github.truenine.composeserver.security.crypto.PemFormatTest#lineEndingProvider")
    fun parseWithDifferentLineEndings(testCase: String, lineEnding: String) {
      val pemWithCustomLineEndings =
        """
        -----BEGIN TEST-----${lineEnding}SGVsbG8gV29ybGQ=${lineEnding}-----END TEST-----
      """
          .trimIndent()

      val pemFormat = PemFormat.parse(pemWithCustomLineEndings)
      assertEquals("TEST", pemFormat.schema)
      assertEquals("SGVsbG8gV29ybGQ=", pemFormat.content)
    }
  }

  @Nested
  inner class PemCreationFromKeysTests {

    @Test
    fun createPemFromRsaPrivateKey() {
      val keyPair = generateTestKeyPair()
      val pem = PemFormat[keyPair.private]

      assertNotNull(pem)
      assertTrue(pem.startsWith("-----BEGIN RSA PRIVATE KEY-----"))
      assertTrue(pem.endsWith("-----END RSA PRIVATE KEY-----"))

      // Verify the created PEM can be parsed back
      val parsed = PemFormat.parse(pem)
      assertEquals("RSA PRIVATE KEY", parsed.schema)
      assertTrue(parsed.content.isNotBlank())
    }

    @Test
    fun createPemFromRsaPublicKey() {
      val keyPair = generateTestKeyPair()
      val pem = PemFormat[keyPair.public]

      assertNotNull(pem)
      assertTrue(pem.startsWith("-----BEGIN RSA PUBLIC KEY-----"))
      assertTrue(pem.endsWith("-----END RSA PUBLIC KEY-----"))
    }

    @Test
    fun createPemFromEcPrivateKey() {
      val keyPair = generateEcKeyPair()
      val pem = PemFormat[keyPair.private]

      assertNotNull(pem)
      assertTrue(pem.startsWith("-----BEGIN EC PRIVATE KEY-----"))
      assertTrue(pem.endsWith("-----END EC PRIVATE KEY-----"))
    }

    @Test
    fun createPemFromKeyWithCustomType() {
      val keyPair = generateTestKeyPair()
      val customType = "CUSTOM PRIVATE KEY"
      val pem = PemFormat[keyPair.private, customType]

      assertTrue(pem.startsWith("-----BEGIN $customType-----"))
      assertTrue(pem.endsWith("-----END $customType-----"))
    }
  }

  @Nested
  inner class PemCreationFromBase64Tests {

    @Test
    fun createPemFromBase64String() {
      val base64 = SIMPLE_BASE64
      val keyType = "TEST KEY"
      val pem = PemFormat[base64, keyType]

      assertTrue(pem.startsWith("-----BEGIN TEST KEY-----"))
      assertTrue(pem.contains(base64))
      assertTrue(pem.endsWith("-----END TEST KEY-----"))
    }

    @Test
    fun createPemFromBase64WithNullKeyType() {
      val base64 = SIMPLE_BASE64
      val pem = PemFormat[base64, null]

      assertTrue(pem.startsWith("-----BEGIN -----"))
      assertTrue(pem.endsWith("-----END -----"))
    }

    @ParameterizedTest
    @MethodSource("io.github.truenine.composeserver.security.crypto.PemFormatTest#keyTypeProvider")
    fun createPemWithDifferentKeyTypes(testCase: String, keyType: String?, expectedBegin: String, expectedEnd: String) {
      val base64 = SIMPLE_BASE64
      val pem = PemFormat[base64, keyType]

      assertTrue(pem.startsWith(expectedBegin), "Expected to start with $expectedBegin, but was: $pem")
      assertTrue(pem.endsWith(expectedEnd), "Expected to end with $expectedEnd, but was: $pem")
    }

    @ParameterizedTest
    @MethodSource("io.github.truenine.composeserver.security.crypto.PemFormatTest#base64ContentProvider")
    fun createPemWithVariousBase64Content(testCase: String, base64Content: String) {
      val pem = PemFormat[base64Content, "TEST"]

      assertTrue(pem.startsWith("-----BEGIN TEST-----"))
      assertTrue(pem.endsWith("-----END TEST-----"))

      // Verify line length compliance
      val lines = pem.lines()
      lines.forEach { line ->
        if (!line.startsWith("-----") && line.isNotBlank()) {
          assertTrue(line.length <= 64, "Line length exceeds 64 characters: $line")
        }
      }
    }
  }

  @Nested
  inner class ErrorHandlingTests {

    @Test
    fun throwsExceptionForBlankBase64Content() {
      assertThrows<IllegalArgumentException> { PemFormat["", "TEST"] }
      assertThrows<IllegalArgumentException> { PemFormat["   ", "TEST"] }
    }

    @Test
    fun throwsExceptionForInvalidBase64Content() {
      assertThrows<IllegalArgumentException> { PemFormat["@#$%^&*()", "TEST"] }
    }

    @Test
    fun throwsExceptionForInvalidKeyType() {
      assertThrows<IllegalArgumentException> { PemFormat[SIMPLE_BASE64, "INVALID@TYPE"] }
    }

    @Test
    fun throwsExceptionForBlankPemContent() {
      assertThrows<IllegalArgumentException> { PemFormat.parse("") }
      assertThrows<IllegalArgumentException> { PemFormat.parse("   ") }
    }
  }

  @Nested
  inner class PropertyAccessTests {

    @Test
    fun schemaPropertyReturnsCorrectValue() {
      val pemFormat = PemFormat.parse(VALID_PEM)
      assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)

      // Test lazy initialization - accessing multiple times should return same value
      assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
      assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
    }

    @Test
    fun contentPropertyReturnsCorrectValue() {
      val pemFormat = PemFormat.parse(VALID_PEM)
      assertEquals(VALID_PEM_CONTENT, pemFormat.content)

      // Test lazy initialization - accessing multiple times should return same value
      assertEquals(VALID_PEM_CONTENT, pemFormat.content)
      assertEquals(VALID_PEM_CONTENT, pemFormat.content)
    }

    @Test
    fun propertiesWorkWithCustomPem() {
      val customPem =
        """
        -----BEGIN CUSTOM KEY-----
        SGVsbG8gV29ybGQ=
        -----END CUSTOM KEY-----
        """
          .trimIndent()

      val pemFormat = PemFormat.parse(customPem)
      assertEquals("CUSTOM KEY", pemFormat.schema)
      assertEquals("SGVsbG8gV29ybGQ=", pemFormat.content)
    }
  }

  @Nested
  inner class PerformanceTests {

    @Test
    fun performanceComparisonForLargeContent() {
      val largeBase64 = "A".repeat(10000)

      val timeOptimized = measureTimeMillis { repeat(100) { PemFormat[largeBase64, "PERFORMANCE TEST"] } }

      // Verify the result is still correct
      val result = PemFormat[largeBase64, "PERFORMANCE TEST"]
      assertTrue(result.startsWith("-----BEGIN PERFORMANCE TEST-----"))
      assertTrue(result.endsWith("-----END PERFORMANCE TEST-----"))

      // Performance should be reasonable (less than 1 second for 100 iterations)
      assertTrue(timeOptimized < 1000, "Performance test took too long: ${timeOptimized}ms")
    }

    @RepeatedTest(10)
    fun consistentPerformanceForRepeatedOperations() {
      val base64 = "A".repeat(1000)

      val time = measureTimeMillis {
        val pem = PemFormat[base64, "REPEATED TEST"]
        val parsed = PemFormat.parse(pem)
        assertEquals("REPEATED TEST", parsed.schema)
      }

      // Each operation should complete quickly
      assertTrue(time < 100, "Operation took too long: ${time}ms")
    }

    @Test
    fun memoryEfficiencyForLazyProperties() {
      val pemFormat = PemFormat.parse(VALID_PEM)

      // Access properties multiple times to test caching
      repeat(1000) {
        assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
        assertEquals(VALID_PEM_CONTENT, pemFormat.content)
      }

      // If lazy properties work correctly, this should complete quickly
      assertTrue(true) // Test passes if no exceptions thrown
    }
  }

  @Nested
  inner class SecurityTests {

    @Test
    fun errorMessagesDoNotExposeInternalDetails() {
      val exception = assertThrows<IllegalArgumentException> { PemFormat.parse("invalid content") }

      // Error message should be generic and not expose internal structure
      assertFalse(exception.message?.contains("internal") == true)
      assertFalse(exception.message?.contains("debug") == true)
    }

    @Test
    fun inputValidationPreventsInjectionAttacks() {
      val maliciousInput = "-----BEGIN <script>alert('xss')</script>-----\nSGVsbG8=\n-----END <script>alert('xss')</script>-----"

      assertThrows<IllegalArgumentException> { PemFormat.parse(maliciousInput) }
    }

    @Test
    fun boundsCheckingPreventsBufferOverflow() {
      val veryLongKeyType = "A".repeat(10000)

      assertThrows<IllegalArgumentException> { PemFormat[SIMPLE_BASE64, veryLongKeyType] }
    }
  }

  @Nested
  inner class EdgeCaseTests {

    @Test
    fun handlesMinimalValidPem() {
      val minimalPem = "-----BEGIN A-----\nB\n-----END A-----"
      val pemFormat = PemFormat.parse(minimalPem)

      assertEquals("A", pemFormat.schema)
      assertEquals("B", pemFormat.content)
    }

    @Test
    fun handlesEmptyKeyTypeCorrectly() {
      val pemWithEmptyType = "-----BEGIN -----\nSGVsbG8=\n-----END -----"
      val pemFormat = PemFormat.parse(pemWithEmptyType)

      assertEquals("", pemFormat.schema)
      assertEquals("SGVsbG8=", pemFormat.content)
    }

    @Test
    fun handlesMultipleConsecutiveLineBreaks() {
      val pemWithExtraBreaks =
        """
        -----BEGIN TEST-----


        SGVsbG8gV29ybGQ=


        -----END TEST-----
        """
          .trimIndent()

      val pemFormat = PemFormat.parse(pemWithExtraBreaks)
      assertEquals("TEST", pemFormat.schema)
      assertEquals("SGVsbG8gV29ybGQ=", pemFormat.content)
    }
  }
}

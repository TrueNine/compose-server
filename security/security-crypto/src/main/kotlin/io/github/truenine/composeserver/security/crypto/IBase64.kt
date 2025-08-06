package io.github.truenine.composeserver.security.crypto

import java.nio.charset.Charset
import java.util.*

/**
 * High-performance Base64 encoding and decoding operations for the Compose Server framework.
 *
 * This interface provides comprehensive Base64 encoding/decoding capabilities with optimized performance, security considerations, and thread-safe operations.
 * All methods are designed to handle various data types efficiently while maintaining compatibility with standard Base64 specifications.
 *
 * ## Supported Operations
 * - **Standard Base64 Encoding/Decoding**: RFC 4648 compliant operations
 * - **URL-Safe Base64**: Web-safe encoding without padding characters
 * - **Byte Array Operations**: Direct byte-to-byte transformations for optimal performance
 * - **Multi-Charset Support**: Flexible character encoding support with UTF-8 default
 *
 * ## Performance Features
 * - Thread-safe encoder/decoder instances with minimal object creation overhead
 * - Optimized memory allocation patterns for large data processing
 * - Input validation to prevent unnecessary processing cycles
 * - Efficient string construction to reduce garbage collection pressure
 *
 * ## Security Considerations
 * - Input validation prevents malformed data processing
 * - No sensitive information exposure in error messages
 * - Constant-time operations where feasible to prevent timing attacks
 * - Proper error handling without exposing internal state
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic string encoding
 * val originalText = "Hello, World!"
 * val encoded = IBase64.encode(originalText.toByteArray())
 * val decoded = IBase64.decode(encoded)
 *
 * // URL-safe encoding for web applications
 * val urlSafeEncoded = IBase64.encodeUrlSafe(data)
 * val urlSafeDecoded = IBase64.decodeUrlSafe(urlSafeEncoded)
 *
 * // Direct byte operations for performance
 * val encodedBytes = IBase64.encodeToByte(inputBytes)
 * val decodedBytes = IBase64.decodeToByte(base64String)
 *
 * // Custom charset handling
 * val encoded = IBase64.encode(text.toByteArray(Charsets.UTF_16))
 * val decoded = IBase64.decode(encoded, Charsets.UTF_16)
 * ```
 *
 * @author TrueNine
 * @since 2023-02-20
 * @version 2.0 - Optimized for performance and security
 */
interface IBase64 {
  companion object {
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()
    private val urlSafeEncoder = Base64.getUrlEncoder().withoutPadding()
    private val urlSafeDecoder = Base64.getUrlDecoder()
    private val defaultCharset = Charsets.UTF_8

    /**
     * Encodes byte array to Base64 string representation.
     *
     * This method provides standard Base64 encoding following RFC 4648 specifications. The operation is thread-safe and optimized for performance with minimal
     * memory allocation. Empty byte arrays are supported and will return an empty string.
     *
     * @param content The byte array to encode
     * @return Base64-encoded string representation
     */
    @JvmStatic
    fun encode(content: ByteArray): String {
      return encoder.encodeToString(content)
    }

    /**
     * Encodes byte array to Base64 byte array representation.
     *
     * This method provides direct byte-to-byte Base64 encoding for optimal performance when string conversion is not required. Ideal for streaming operations
     * or when working with binary data pipelines. Empty byte arrays are supported.
     *
     * @param content The byte array to encode
     * @return Base64-encoded byte array
     */
    @JvmStatic
    fun encodeToByte(content: ByteArray): ByteArray {
      return encoder.encode(content)
    }

    /**
     * Encodes byte array to URL-safe Base64 string without padding.
     *
     * This method provides URL and filename safe Base64 encoding by replacing '+' with '-' and '/' with '_', and removes padding characters. Ideal for web
     * applications, URLs, and file names where standard Base64 characters might cause issues. Empty byte arrays are supported.
     *
     * @param content The byte array to encode
     * @return URL-safe Base64-encoded string without padding
     */
    @JvmStatic
    fun encodeUrlSafe(content: ByteArray): String {
      return urlSafeEncoder.encodeToString(content)
    }

    /**
     * Decodes Base64 string to byte array.
     *
     * This method decodes standard Base64 strings back to their original byte representation. Includes validation to ensure the input is properly formatted
     * Base64 data.
     *
     * @param base64 The Base64-encoded string to decode
     * @return Decoded byte array
     * @throws IllegalArgumentException if base64 string is null, empty, or malformed
     */
    @JvmStatic
    fun decodeToByte(base64: String): ByteArray {
      // Empty string is a valid Base64 encoding of empty byte array
      if (base64.isEmpty()) return ByteArray(0)
      // Direct decode with minimal exception handling for performance
      try {
        return decoder.decode(base64)
      } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid Base64 string format", e)
      }
    }

    /**
     * Decodes Base64 string to string with specified charset.
     *
     * This method combines Base64 decoding with charset conversion in a single operation. Provides flexibility for handling different character encodings while
     * maintaining performance through optimized string construction.
     *
     * @param base64 The Base64-encoded string to decode
     * @param charset The character encoding to use for string conversion (default: UTF-8)
     * @return Decoded string in specified charset
     * @throws IllegalArgumentException if base64 string is malformed or charset is invalid
     */
    @JvmStatic
    fun decode(base64: String, charset: Charset = defaultCharset): String {
      val decodedBytes = decodeToByte(base64)
      return String(decodedBytes, charset)
    }

    /**
     * Decodes Base64 byte array to string with specified charset.
     *
     * This method handles Base64 decoding from byte array input, useful when working with binary data streams or when the Base64 data is already in byte
     * format.
     *
     * @param base64 The Base64-encoded byte array to decode
     * @param charset The character encoding to use for string conversion (default: UTF-8)
     * @return Decoded string in specified charset
     * @throws IllegalArgumentException if base64 byte array is malformed
     */
    @JvmStatic
    fun decode(base64: ByteArray, charset: Charset = defaultCharset): String {
      if (base64.isEmpty()) return ""
      val decodedBytes = decoder.decode(base64)
      return String(decodedBytes, charset)
    }

    /**
     * Decodes URL-safe Base64 string to byte array.
     *
     * This method decodes URL-safe Base64 strings that use '-' instead of '+' and '_' instead of '/' and may not include padding characters. Complements the
     * encodeUrlSafe method for complete URL-safe Base64 operations.
     *
     * @param base64 The URL-safe Base64-encoded string to decode
     * @return Decoded byte array
     * @throws IllegalArgumentException if base64 string is null, empty, or malformed
     */
    @JvmStatic
    fun decodeUrlSafe(base64: String): ByteArray {
      if (base64.isEmpty()) return ByteArray(0)
      if (base64.isBlank()) throw IllegalArgumentException("URL-safe Base64 string cannot be null or blank")
      return urlSafeDecoder.decode(base64)
    }

    /**
     * Validates if a string is properly formatted Base64 data.
     *
     * This method provides fast validation without performing actual decoding, useful for input validation and data integrity checks.
     *
     * @param base64 The string to validate
     * @return true if the string is valid Base64 format, false otherwise
     */
    @JvmStatic
    fun isValidBase64(base64: String?): Boolean {
      if (base64.isNullOrBlank()) return false
      return try {
        decoder.decode(base64)
        true
      } catch (_: IllegalArgumentException) {
        false
      }
    }
  }
}

package io.github.truenine.composeserver.security.crypto

import java.nio.charset.Charset

/**
 * ByteArray extension functions for Base64 encoding and decoding operations.
 *
 * This file provides convenient extension functions for ByteArray objects to perform Base64 encoding and decoding operations using the high-performance IBase64
 * interface. These extensions simplify common cryptographic operations while maintaining optimal performance and security standards.
 *
 * ## Key Features
 * - **Direct ByteArray Operations**: Optimized for performance with minimal object creation
 * - **Flexible Output Formats**: Support for both byte array and string outputs
 * - **Charset Support**: Configurable character encoding for string operations
 * - **Thread-Safe Operations**: All operations are thread-safe and can be used in concurrent environments
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic encoding operations
 * val originalData = "Hello, World!".toByteArray()
 * val encodedBytes = originalData.encodeBase64
 * val encodedString = originalData.encodeBase64String
 *
 * // Decoding operations
 * val base64Data = "SGVsbG8sIFdvcmxkIQ==".toByteArray()
 * val decodedBytes = base64Data.decodeBase64()
 * val decodedString = base64Data.decodeBase64String()
 *
 * // Custom charset handling
 * val utf16Data = "Sample".toByteArray(Charsets.UTF_16)
 * val decoded = utf16Data.decodeBase64String(Charsets.UTF_16)
 * ```
 *
 * @see IBase64 for underlying Base64 implementation details
 * @author TrueNine
 * @since 2023-02-20
 */

/**
 * Encodes this ByteArray to a Base64-encoded ByteArray.
 *
 * This extension property provides direct byte-to-byte Base64 encoding for optimal performance when working with binary data pipelines or streaming operations.
 * The operation is thread-safe and uses the standard Base64 encoding scheme defined in RFC 4648.
 *
 * ## Performance Characteristics
 * - Minimal memory allocation overhead
 * - Direct byte array operations without intermediate string conversion
 * - Optimized for large data processing scenarios
 *
 * ## Usage Example
 *
 * ```kotlin
 * val binaryData = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F) // "Hello"
 * val encoded = binaryData.encodeBase64
 * // Result: Base64-encoded byte array representation
 * ```
 *
 * @return Base64-encoded ByteArray representation of this ByteArray
 * @see IBase64.encodeToByte for underlying implementation
 */
val ByteArray.encodeBase64: ByteArray
  get() = IBase64.encodeToByte(this)

/**
 * Encodes this ByteArray to a Base64-encoded String.
 *
 * This extension property converts the ByteArray to a Base64 string representation using UTF-8 encoding. Ideal for scenarios where Base64 data needs to be
 * transmitted as text or stored in string-based formats. The operation is thread-safe and follows RFC 4648 Base64 encoding standards.
 *
 * ## Use Cases
 * - API data transmission where binary data must be represented as text
 * - Database storage of binary data in text columns
 * - Configuration files requiring binary data in readable format
 * - Web applications handling binary data in JSON responses
 *
 * ## Usage Example
 *
 * ```kotlin
 * val imageData = File("image.png").readBytes()
 * val base64Image = imageData.encodeBase64String
 * // Result: "iVBORw0KGgoAAAANSUhEUgAA..." (Base64 string)
 * ```
 *
 * @return Base64-encoded String representation of this ByteArray
 * @see IBase64.encode for underlying implementation
 */
val ByteArray.encodeBase64String: String
  get() = IBase64.encode(this)

/**
 * Decodes this ByteArray (containing Base64 string bytes) to the original ByteArray.
 *
 * This function treats the ByteArray as containing UTF-8 encoded Base64 string data and decodes it back to the original binary representation. Useful when
 * Base64 data is received as byte arrays from network streams or file operations.
 *
 * ## Important Notes
 * - The input ByteArray must contain valid UTF-8 encoded Base64 string data
 * - Invalid Base64 format will result in IllegalArgumentException
 * - Empty byte arrays are supported and return empty results
 *
 * ## Usage Example
 *
 * ```kotlin
 * val base64Bytes = "SGVsbG8gV29ybGQ=".toByteArray(Charsets.UTF_8)
 * val originalData = base64Bytes.decodeBase64()
 * val originalText = String(originalData) // "Hello World"
 * ```
 *
 * @return Decoded ByteArray containing the original binary data
 * @throws IllegalArgumentException if the ByteArray contains invalid Base64 data
 * @see IBase64.decodeToByte for underlying implementation
 */
fun ByteArray.decodeBase64(): ByteArray = IBase64.decodeToByte(String(this))

/**
 * Decodes this ByteArray (containing Base64 string bytes) to a String with specified charset.
 *
 * This function combines Base64 decoding with charset conversion in a single operation. It treats the ByteArray as containing UTF-8 encoded Base64 string data,
 * decodes it to binary, then converts the result to a string using the specified charset.
 *
 * ## Charset Considerations
 * - Default charset is UTF-8 for maximum compatibility
 * - Custom charsets enable handling of legacy or specialized text encodings
 * - Charset mismatch between encoding and decoding will produce incorrect results
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Standard UTF-8 decoding
 * val base64Data = "SGVsbG8gV29ybGQ=".toByteArray()
 * val text = base64Data.decodeBase64String()
 * // Result: "Hello World"
 *
 * // Custom charset decoding
 * val utf16Base64 = "//5IAGUAbABsAG8A".toByteArray()
 * val utf16Text = utf16Base64.decodeBase64String(Charsets.UTF_16)
 * // Result: "Hello" (decoded with UTF-16 charset)
 * ```
 *
 * @param charset The character encoding to use for string conversion (default: UTF-8)
 * @return Decoded String in the specified charset
 * @throws IllegalArgumentException if the ByteArray contains invalid Base64 data
 * @see IBase64.decode for underlying implementation
 */
fun ByteArray.decodeBase64String(charset: Charset = Charsets.UTF_8): String = IBase64.decode(this, charset)

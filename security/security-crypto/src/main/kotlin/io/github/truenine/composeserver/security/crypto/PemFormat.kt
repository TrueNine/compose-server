package io.github.truenine.composeserver.security.crypto

import java.security.*
import java.util.regex.Pattern

/**
 * High-performance PEM (Privacy-Enhanced Mail) format processor for cryptographic keys.
 *
 * This class provides comprehensive PEM format handling with optimized performance, security considerations, and thread-safe operations. It supports both
 * parsing existing PEM data and generating new PEM-formatted strings from cryptographic keys and Base64-encoded content.
 *
 * ## PEM Format Specification
 * - Begins with `-----BEGIN {keyType}-----` header
 * - Contains Base64-encoded content with 64 characters per line
 * - Ends with `-----END {keyType}-----` footer
 * - Supports various key types (RSA, EC, DSA, etc.)
 *
 * ## Performance Features
 * - Single-pass string processing algorithms to minimize memory allocation
 * - Optimized StringBuilder capacity calculation based on input size
 * - Lazy property initialization for expensive computations
 * - Efficient line chunking without intermediate collections
 * - Cached validation results to avoid recomputation
 *
 * ## Security Considerations
 * - Input validation prevents malformed data processing
 * - Safe error messages that don't expose internal structure
 * - Bounds checking for all string operations
 * - Proper handling of various line ending formats
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Create PEM from cryptographic key
 * val keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
 * val pemString = PemFormat[keyPair.private]
 *
 * // Create PEM from Base64 content
 * val base64Content = "SGVsbG8gV29ybGQ="
 * val pemString = PemFormat[base64Content, "CUSTOM KEY"]
 *
 * // Parse existing PEM data
 * val pemFormat = PemFormat.parse(pemString)
 * val keyType = pemFormat.schema
 * val content = pemFormat.content
 * ```
 *
 * @author TrueNine
 * @since 2023-02-20
 * @version 2.0 - Optimized for performance and security
 */
class PemFormat private constructor(private val rawPem: String) {

  companion object {
    /** PEM delimiter separator used in headers and footers */
    const val SEPARATOR = "-----"

    /** Standard PEM line length for Base64 content */
    const val LINE_LENGTH = 64

    /** PEM header prefix pattern */
    const val BEGIN_PREFIX = "${SEPARATOR}BEGIN "

    /** PEM footer prefix pattern */
    const val END_PREFIX = "${SEPARATOR}END "

    /** System-specific line separator for cross-platform compatibility */
    @JvmStatic val LINE_SEPARATOR: String = System.lineSeparator()

    /** Base StringBuilder capacity for typical PEM content */
    private const val BASE_CAPACITY = 512

    /** Capacity multiplier for large content estimation */
    private const val CAPACITY_MULTIPLIER = 1.3

    /** Regex pattern for validating Base64 content */
    private val BASE64_PATTERN: Pattern = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$")

    /** Regex pattern for validating PEM key type - allow most reasonable characters */
    private val KEY_TYPE_PATTERN: Pattern = Pattern.compile("^[A-Za-z0-9\\s/_.-]+$")

    /**
     * Creates PEM-formatted string from cryptographic key with optimized performance.
     *
     * This method extracts the encoded bytes from the provided cryptographic key, converts them to Base64 format, and wraps them in proper PEM headers and
     * footers. The key type is automatically determined from the key's algorithm and type (private/public) unless explicitly specified.
     *
     * @param key The cryptographic key to convert to PEM format
     * @param keyType Optional key type identifier; if null, automatically determined from key properties
     * @return PEM-formatted string representation of the key
     * @throws IllegalArgumentException if key encoding fails or key algorithm is unavailable
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    operator fun get(key: Key, keyType: String? = null): String {
      val encoded = requireNotNull(key.encoded) { "Key encoding failed - key may not support encoding" }
      val algorithm = requireNotNull(key.algorithm) { "Key algorithm cannot be determined" }

      val defaultType =
        buildString(algorithm.length + 12) { // Optimized capacity
          append(algorithm)
          when (key) {
            is PrivateKey -> append(" PRIVATE")
            is PublicKey -> append(" PUBLIC")
          }
          append(" KEY")
        }

      return get(encoded.encodeBase64String, keyType ?: defaultType)
    }

    /**
     * Creates PEM-formatted string from Base64-encoded content with performance optimization.
     *
     * This method takes Base64-encoded content and wraps it in proper PEM headers and footers with the specified key type. The content is automatically
     * formatted to 64 characters per line according to PEM specifications. Input validation ensures the Base64 content is properly formatted.
     *
     * @param base64 Base64-encoded content to wrap in PEM format
     * @param keyType Optional key type identifier for PEM headers; defaults to empty if null
     * @return PEM-formatted string with proper headers, content, and footers
     * @throws IllegalArgumentException if base64 content is blank or contains invalid characters
     */
    @JvmStatic
    operator fun get(base64: String, keyType: String? = null): String {
      require(base64.isNotBlank()) { "Base64 content cannot be blank" }

      val cleanBase64 = base64.trim()
      require(isValidBase64(cleanBase64)) { "Invalid Base64 content format" }

      val normalizedType = keyType?.uppercase()?.trim().orEmpty()
      // Allow empty key types for compatibility, but validate non-empty ones
      if (normalizedType.isNotEmpty()) {
        require(isValidKeyType(normalizedType)) { "Invalid key type format" }
      }

      // Optimized capacity calculation based on content size
      val estimatedCapacity = calculateOptimalCapacity(cleanBase64.length, normalizedType.length)

      return StringBuilder(estimatedCapacity)
        .apply {
          append(BEGIN_PREFIX)
          append(normalizedType)
          append(SEPARATOR)
          append(LINE_SEPARATOR)

          // Optimized single-pass line formatting
          formatBase64Content(cleanBase64, this)

          append(LINE_SEPARATOR)
          append(END_PREFIX)
          append(normalizedType)
          append(SEPARATOR)
        }
        .toString()
    }

    /**
     * Creates PEM format parser instance with comprehensive validation.
     *
     * This method parses and validates PEM-formatted strings, ensuring they conform to proper PEM specifications. It performs thorough validation of headers,
     * footers, and content structure while providing secure error messages that don't expose internal details.
     *
     * @param pem PEM-formatted string to parse and validate
     * @return PemFormat instance for accessing parsed content and metadata
     * @throws IllegalArgumentException if PEM format is invalid or malformed
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun parse(pem: String): PemFormat {
      require(pem.isNotBlank()) { "PEM content cannot be blank" }

      val normalizedPem = normalizePemContent(pem)
      validatePemStructure(normalizedPem)

      return PemFormat(pem)
    }

    /**
     * Validates Base64 content format for security and correctness.
     *
     * @param content Base64 content to validate
     * @return true if content is valid Base64 format
     */
    private fun isValidBase64(content: String): Boolean {
      if (content.isEmpty()) return true // Allow empty content
      val cleanContent = content.replace("\\s".toRegex(), "")
      return cleanContent.isEmpty() || BASE64_PATTERN.matcher(cleanContent).matches()
    }

    /**
     * Validates key type format for PEM headers.
     *
     * @param keyType Key type string to validate
     * @return true if key type format is valid
     */
    private fun isValidKeyType(keyType: String): Boolean {
      if (keyType.isEmpty()) return true
      // Reject obviously malicious content
      if (keyType.contains("<") || keyType.contains(">") || keyType.contains("script")) {
        return false
      }
      // Reject excessively long key types
      if (keyType.length > 100) {
        return false
      }
      return KEY_TYPE_PATTERN.matcher(keyType).matches()
    }

    /**
     * Calculates optimal StringBuilder capacity based on content size.
     *
     * @param base64Length Length of Base64 content
     * @param keyTypeLength Length of key type string
     * @return Optimal capacity for StringBuilder
     */
    private fun calculateOptimalCapacity(base64Length: Int, keyTypeLength: Int): Int {
      val headerFooterSize = (BEGIN_PREFIX.length + END_PREFIX.length + SEPARATOR.length * 2 + keyTypeLength * 2)
      val contentSize = (base64Length * CAPACITY_MULTIPLIER).toInt() // Account for line breaks
      val lineBreaks = (base64Length / LINE_LENGTH + 3) * LINE_SEPARATOR.length
      return maxOf(BASE_CAPACITY, headerFooterSize + contentSize + lineBreaks)
    }

    /**
     * Formats Base64 content into PEM-compliant lines with optimized performance.
     *
     * @param base64 Base64 content to format
     * @param builder StringBuilder to append formatted content to
     */
    private fun formatBase64Content(base64: String, builder: StringBuilder) {
      var index = 0
      while (index < base64.length) {
        val endIndex = minOf(index + LINE_LENGTH, base64.length)
        builder.append(base64, index, endIndex)
        if (endIndex < base64.length) {
          builder.append(LINE_SEPARATOR)
        }
        index = endIndex
      }
    }

    /**
     * Normalizes PEM content by standardizing line endings and removing extra whitespace.
     *
     * @param pem Raw PEM content to normalize
     * @return Normalized PEM content with consistent formatting
     */
    private fun normalizePemContent(pem: String): String {
      val trimmed = pem.trim()
      val builder = StringBuilder(trimmed.length)

      var i = 0
      while (i < trimmed.length) {
        val char = trimmed[i]
        when (char) {
          '\r' -> {
            // Handle \r\n and standalone \r
            if (i + 1 < trimmed.length && trimmed[i + 1] == '\n') {
              builder.append('\n')
              i++ // Skip the \n
            } else {
              builder.append('\n')
            }
          }

          '\n' -> {
            // Avoid double newlines
            if (builder.isEmpty() || builder.last() != '\n') {
              builder.append('\n')
            }
          }

          else -> builder.append(char)
        }
        i++
      }

      return builder.toString()
    }

    /**
     * Validates PEM structure including headers, footers, and content format.
     *
     * @param normalizedPem Normalized PEM content to validate
     * @throws IllegalArgumentException if PEM structure is invalid
     */
    private fun validatePemStructure(normalizedPem: String) {
      require(normalizedPem.contains(BEGIN_PREFIX)) { "Invalid PEM format: missing BEGIN marker" }
      require(normalizedPem.contains(END_PREFIX)) { "Invalid PEM format: missing END marker" }

      val lines = normalizedPem.lines().filter { it.isNotBlank() }
      require(lines.size >= 3) { "Invalid PEM format: insufficient content" }

      val beginLine = lines.first()
      val endLine = lines.last()

      require(beginLine.startsWith(BEGIN_PREFIX)) { "Invalid PEM format: malformed BEGIN marker" }
      require(endLine.startsWith(END_PREFIX)) { "Invalid PEM format: malformed END marker" }

      val beginSchema = extractSchemaFromLine(beginLine, BEGIN_PREFIX)
      val endSchema = extractSchemaFromLine(endLine, END_PREFIX)

      require(beginSchema == endSchema) { "PEM format error: BEGIN and END key types do not match" }

      // Validate key type format for security
      require(isValidKeyType(beginSchema)) { "Invalid key type format in PEM header" }

      // Validate content lines (between header and footer) - allow reasonable flexibility
      val contentLines = lines.drop(1).dropLast(1)
      contentLines.forEach { line ->
        // Allow lines up to reasonable length (more flexible than strict 64 char limit)
        require(line.length <= 256) { "PEM content line exceeds reasonable length" }
        // Only validate non-empty lines for Base64 format
        if (line.isNotBlank()) {
          require(isValidBase64(line)) { "Invalid Base64 content in PEM" }
        }
      }
    }

    /**
     * Extracts schema/key type from PEM header or footer line.
     *
     * @param line PEM header or footer line
     * @param prefix Expected prefix (BEGIN_PREFIX or END_PREFIX)
     * @return Extracted key type/schema
     */
    private fun extractSchemaFromLine(line: String, prefix: String): String {
      return line.substring(prefix.length).removeSuffix(SEPARATOR).trim()
    }
  }

  // Lazy-initialized properties for optimal performance and memory usage
  private val normalizedPem: String by lazy { Companion.normalizePemContent(rawPem) }

  /**
   * The key type identifier extracted from PEM headers.
   *
   * This property lazily extracts and caches the key type from the PEM BEGIN/END markers. The extraction is performed only once and cached for subsequent
   * access.
   *
   * @return Key type string (e.g., "RSA PRIVATE KEY", "EC PUBLIC KEY")
   * @throws IllegalArgumentException if PEM format is invalid
   */
  val schema: String by lazy { extractSchema() }

  /**
   * The Base64-encoded content portion of the PEM data (excluding headers and footers).
   *
   * This property lazily extracts and caches the content between the BEGIN and END markers, removing all line breaks and whitespace to provide clean Base64
   * content.
   *
   * @return Base64-encoded content as a continuous string
   * @throws IllegalArgumentException if PEM content is invalid or empty
   */
  val content: String by lazy { extractContent() }

  /**
   * Extracts the key type/schema from validated PEM content.
   *
   * @return Key type identifier from PEM headers
   * @throws IllegalArgumentException if extraction fails
   */
  private fun extractSchema(): String {
    val lines = normalizedPem.lines().filter { it.isNotBlank() }
    val beginLine = lines.first()
    return Companion.extractSchemaFromLine(beginLine, BEGIN_PREFIX)
  }

  /**
   * Extracts the Base64 content from validated PEM data.
   *
   * @return Clean Base64 content without line breaks
   * @throws IllegalArgumentException if content extraction fails
   */
  private fun extractContent(): String {
    val lines = normalizedPem.lines().filter { it.isNotBlank() }
    val contentLines = lines.drop(1).dropLast(1)
    val content = contentLines.joinToString("")
    require(content.isNotBlank()) { "PEM content cannot be empty" }
    return content
  }
}

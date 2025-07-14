package io.github.truenine.composeserver

/**
 * Specialized interface for string-based typed enumerations.
 *
 * This interface extends [IAnyTyping] to provide type-safe handling of enums that use string values as their underlying representation. It's particularly
 * useful for enums that represent textual constants, codes, or identifiers that need to be serialized as strings in JSON, stored as VARCHAR in databases, or
 * used in HTTP parameters.
 *
 * ## Design Rationale
 *
 * String-based enums are essential for:
 * - API responses that need human-readable values
 * - Configuration parameters with textual identifiers
 * - Database columns that store coded values
 * - Internationalization and localization keys
 *
 * ## Usage Example
 *
 * ```kotlin
 * enum class Language(private val code: String) : IStringTyping {
 *   ENGLISH("en"),
 *   CHINESE("zh"),
 *   JAPANESE("ja");
 *
 *   @get:JsonValue
 *   override val value: String = code
 *
 *   companion object {
 *     @JvmStatic
 *     operator fun get(code: String?): Language? = entries.find { it.value == code }
 *   }
 * }
 * ```
 *
 * ## Serialization Behavior
 *
 * When serialized by Jackson, the enum will be represented by its string value:
 * ```json
 * {
 *   "language": "en"
 * }
 * ```
 *
 * @see IAnyTyping for the base interface
 * @see IIntTyping for integer-based enums
 * @author TrueNine
 * @since 2023-05-28
 */
interface IStringTyping : IAnyTyping {
  /**
   * The string value of this enum constant.
   *
   * This property narrows the type from [IAnyTyping.value] to ensure that string-based enums always return string values. This enables type-safe operations and
   * better IDE support.
   *
   * @return the string representation of this enum constant
   */
  override val value: String

  companion object {
    /**
     * Default implementation for reverse lookup by string value.
     *
     * This method serves as a placeholder and should be overridden in implementing enum classes to provide actual string-to-enum conversion functionality.
     *
     * @param v the string value to look up
     * @return null in the base implementation, should return the matching enum constant in implementations
     */
    @JvmStatic operator fun get(v: String?): IStringTyping? = null
  }
}

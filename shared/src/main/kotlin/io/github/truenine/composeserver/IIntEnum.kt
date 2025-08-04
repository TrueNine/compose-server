package io.github.truenine.composeserver

/**
 * Specialized interface for integer-based typed enumerations.
 *
 * This interface extends [IAnyEnum] to provide type-safe handling of enums that use integer values as their underlying representation. It's the most common
 * enum type in the framework, particularly useful for status codes, type identifiers, and ordinal-based categorizations that need efficient storage and
 * comparison.
 *
 * ## Design Rationale
 *
 * Integer-based enums are preferred for:
 * - Database storage efficiency (INTEGER vs VARCHAR)
 * - Performance-critical comparisons and sorting
 * - Status codes and error codes
 * - Ordinal-based categorizations
 * - Bitwise operations and flags
 *
 * ## Usage Example
 *
 * ```kotlin
 * @EnumType(EnumType.Strategy.ORDINAL)
 * enum class UserStatus(private val code: Int) : IIntEnum {
 *   @EnumItem(ordinal = 0) INACTIVE(0),
 *   @EnumItem(ordinal = 1) ACTIVE(1),
 *   @EnumItem(ordinal = 2) SUSPENDED(2),
 *   @EnumItem(ordinal = 9999) UNKNOWN(9999);
 *
 *   @get:JsonValue
 *   override val value: Int = code
 *
 *   companion object {
 *     @JvmStatic
 *     operator fun get(code: Int?): UserStatus? = entries.find { it.value == code }
 *   }
 * }
 * ```
 *
 * ## Framework Integration
 * - **Jimmer ORM**: Uses `@EnumType(Strategy.ORDINAL)` for database mapping
 * - **Jackson**: Serializes as numeric values in JSON
 * - **Spring**: Supports automatic conversion from request parameters
 *
 * ## Serialization Behavior
 *
 * When serialized by Jackson, the enum will be represented by its integer value:
 * ```json
 * {
 *   "status": 1
 * }
 * ```
 *
 * @see IAnyEnum for the base interface
 * @see IStringEnum for string-based enums
 * @author TrueNine
 * @since 2023-05-28
 */
interface IIntEnum : IAnyEnum {
  /**
   * The integer value of this enum constant.
   *
   * This property narrows the type from [IAnyEnum.value] to ensure that integer-based enums always return integer values. This enables type-safe arithmetic
   * operations and efficient database storage.
   *
   * @return the integer representation of this enum constant
   */
  override val value: Int

  companion object {
    /**
     * Default implementation for reverse lookup by integer value.
     *
     * This method serves as a placeholder and should be overridden in implementing enum classes to provide actual integer-to-enum conversion functionality.
     *
     * @param v the integer value to look up
     * @return null in the base implementation, should return the matching enum constant in implementations
     */
    @JvmStatic operator fun get(v: Int?): IIntEnum? = null
  }
}

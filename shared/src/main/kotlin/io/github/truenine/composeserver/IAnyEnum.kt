package io.github.truenine.composeserver

/**
 * Base interface for all typed enumerations in the Compose Server framework.
 *
 * This interface provides a unified contract for enum types that need to be serialized/deserialized by various frameworks such as Jackson, Spring converters,
 * and Jimmer ORM. The design enables type-safe enum handling while maintaining compatibility with different serialization mechanisms.
 *
 * ## Design Intent
 *
 * The interface addresses the challenge of consistent enum serialization across different layers of the application stack. By providing a common `value`
 * property, it allows serializers to access the underlying value without knowing the specific enum type.
 *
 * ## Implementation Contract
 *
 * Implementing enums **must** provide a companion object with an `operator fun get` method for reverse lookup. This is a framework convention since Kotlin
 * interfaces cannot define static methods.
 *
 * ## Usage Example
 *
 * ```kotlin
 * enum class Gender(private val v: Int) : IIntEnum {
 *   MALE(1),
 *   FEMALE(0),
 *   UNKNOWN(9999);
 *
 *   @get:JsonValue
 *   override val value: Int = v
 *
 *   companion object {
 *     @JvmStatic
 *     operator fun get(v: Int?): Gender? = entries.find { it.value == v }
 *   }
 * }
 * ```
 *
 * ## Framework Integration
 * - **Jackson**: Automatic serialization via `@JsonValue` on the `value` property
 * - **Spring**: Custom converters use the `get` operator for string-to-enum conversion
 * - **Jimmer ORM**: Database mapping through `@EnumType` annotations
 *
 * @see IStringEnum for string-based enums
 * @see IIntEnum for integer-based enums
 * @author TrueNine
 * @since 2023-05-28
 */
interface IAnyEnum {
  /**
   * The underlying value of this enum constant.
   *
   * This property provides access to the actual value that will be used during serialization and database persistence. The type is intentionally generic to
   * support both string and numeric enum types.
   *
   * @return the underlying value of this enum constant
   */
  val value: Any

  companion object {
    /**
     * Default implementation for reverse lookup by value.
     *
     * This method serves as a placeholder and should be overridden in implementing enum classes. It's part of the framework convention for enum value
     * resolution.
     *
     * @param v the value to look up
     * @return null in the base implementation, should return the matching enum constant in implementations
     */
    @JvmStatic operator fun get(v: Any?): IAnyEnum? = null
  }
}

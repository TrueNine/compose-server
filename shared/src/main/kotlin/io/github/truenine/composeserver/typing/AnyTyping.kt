package io.github.truenine.composeserver.typing

/**
 * # 所有类型枚举的抽象接口
 * 实现此接口，以方便其他序列化程序来读取枚举 实现此接口后，需要手动添加一个 operator fun get 静态方法，提供给 jackson等框架自动调用
 *
 * 由于无法在接口规定静态方法，此算作规约。以下为一个枚举类内部的静态方法示例
 *
 * ```kotlin
 * enum class GenderTyping(private val value: Int) {
 *   // ... other enum constants
 *   ;
 *     @get:JsonValue
 *     override val value = this.v
 *     companion object {
 *       @JvmStatic
 *       operator fun get(v: Int?) = entries.find { it.value == v }
 *     }
 * }
 * ```
 *
 * @author TrueNine
 * @since 2023-05-28
 */
interface AnyTyping {
  /** ## 获取枚举对应的实际值 */
  val value: Any

  companion object {
    @JvmStatic operator fun get(v: Any?): AnyTyping? = null
  }
}

package io.github.truenine.composeserver.typing

/** # 字符型枚举 */
interface StringTyping : AnyTyping {
  override val value: String

  companion object {
    @JvmStatic operator fun get(v: Int?): IntTyping? = null
  }
}

package net.yan100.compose.core.typing

/** # 字符型枚举 */
interface StringTyping : AnyTyping {
  override val value: String

  companion object {
    @JvmStatic operator fun get(v: Int?): IntTyping? = null
  }
}

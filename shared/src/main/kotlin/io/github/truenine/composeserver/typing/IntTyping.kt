package io.github.truenine.composeserver.typing

/** # 数值型枚举 */
interface IntTyping : AnyTyping {

  override val value: Int

  companion object {
    @JvmStatic operator fun get(v: Int?): IntTyping? = null
  }
}

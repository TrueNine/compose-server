package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonValue

/** # 字符型枚举 */
interface StringTyping : AnyTyping {
  @get:JsonValue
  override val value: String

  companion object {
    @JvmStatic
    operator fun get(v: Int?): IntTyping? = null
  }
}

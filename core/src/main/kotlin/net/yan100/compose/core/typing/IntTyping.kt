package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonValue

/** # 数值型枚举 */
interface IntTyping : AnyTyping {
  @get:JsonValue
  override val value: Int

  companion object {
    @JvmStatic
    operator fun get(v: Int?): IntTyping? = null
  }
}

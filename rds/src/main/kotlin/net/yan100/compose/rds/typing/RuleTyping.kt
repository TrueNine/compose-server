package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "规则状态")
enum class RuleTyping(
  private val value: Int
) : IntTyping {
  @Schema(title = "排除")
  EXCLUDE(0),

  @Schema(title = "包含")
  INCLUDE(1),

  @Schema(title = "固定")
  FIXED(2);

  @JsonValue
  override fun getValue(): Int = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?): RuleTyping? = RuleTyping.entries.find { it.value == v }
  }
}

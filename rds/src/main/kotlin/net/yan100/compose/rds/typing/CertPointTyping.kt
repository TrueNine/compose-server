package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "证件印面类型")
enum class CertPointTyping(
  private val v: Int
) : IntTyping {
  @Schema(title = "无要求")
  NONE(0),

  @Schema(title = "正面")
  HEADS(1),

  @Schema(title = "反面")
  TAILS(2),

  @Schema(title = "双面")
  DOUBLE(3),

  @Schema(title = "所有")
  ALL(4),

  @Schema(title = "所有内容")
  ALL_CONTENT(5);

  @JsonValue
  override fun getValue(): Int? = v

  companion object {
    @JvmStatic
    fun findVal(value: Int?) = entries.find { value == it.v }
  }
}

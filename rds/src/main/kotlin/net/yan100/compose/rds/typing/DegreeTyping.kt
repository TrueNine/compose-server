package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

enum class DegreeTyping(
  private val value: Int
) : IntTyping {
  @Schema(title = "文盲")
  NONE(0),

  @Schema(title = "小学")
  MIN(1),

  @Schema(title = "初中")
  HALF(2),

  @Schema(title = "高中")
  HEIGHT(3),

  @Schema(title = "本科")
  BIG(4),

  @Schema(title = "研究生")
  DISCOVERY(5),

  @Schema(title = "博士")
  EXPERT(6),

  @Schema(title = "博士后")
  AFTER_EXPERT(7),

  @Schema(title = "其他")
  OTHER(9999);

  @JsonValue
  override fun getValue() = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = DegreeTyping.entries.find { it.value == v }
  }
}
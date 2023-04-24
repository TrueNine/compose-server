package com.truenine.component.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
/**
 * 性别类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
enum class GenderTyping(
  private val value: Int
) {
  /**
   * 男
   */
  @Schema(title = "男")
  MAN(1),

  /**
   * 女
   */
  @Schema(title = "女")
  WOMAN(0),

  /**
   * 未知
   */
  @Schema(title = "未知")
  UNKNOWN(2);

  @JsonValue
  fun getValue(): Int = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = GenderTyping.values().find { it.value == v }
  }
}

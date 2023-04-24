package com.truenine.component.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 商品改动类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "商品改动类型")
enum class GoodsChangeRecordTyping(
  private val value: Int
) {
  /**
   * 改价格
   */
  @Schema(title = "改价格")
  CHANGE_PRICE(0),

  /**
   * 改标题
   */
  @Schema(title = "改标题")
  CHANGE_TITLE(1);

  @JsonValue
  fun getValue() = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = GoodsChangeRecordTyping.values().find { it.value == v }
  }
}

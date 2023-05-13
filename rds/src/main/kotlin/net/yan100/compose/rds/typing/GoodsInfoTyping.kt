package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * 商品信息分类
 */
@Schema(title = "商品信息分类")
enum class GoodsInfoTyping(
  private val value: Int?
) : IntTyping {
  /**
   * 检索类型
   */
  @Schema(title = "检索类型")
  RETRIEVAL(0),

  /**
   * 商品单位信息
   */
  @Schema(title = "商品单位信息")
  GOODS_UNIT_INFO(1),

  /**
   * 商品单位继承信息
   */
  @Schema(title = "商品单位继承信息")
  GOODS_UNIT_EXTEND_INFO(2);

  @JsonValue
  override fun getValue() = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = GoodsInfoTyping.values().find { it.value == v }
  }
}

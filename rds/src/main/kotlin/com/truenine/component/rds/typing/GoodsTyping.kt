package com.truenine.component.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 商品服务类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "商品类型")
enum class GoodsTyping(private val value: Int) {
  /**
   * 实体商品
   */
  @Schema(title = "实体商品")
  PHYSICAL_GOODS(0),

  /**
   * 服务商品
   */
  @Schema(title = "服务商品")
  SERVICE_GOODS(1),

  /**
   * 虚拟商品
   */
  @Schema(title = "虚拟商品")
  VIRTUAL_GOODS(2);

  @JsonValue
  fun getValue(): Int = this.value

  companion object {
    @JvmStatic
    fun findVal(v: Int?): GoodsTyping? = GoodsTyping.values().find { it.getValue() == v }
  }
}

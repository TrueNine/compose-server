package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * 商品服务类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "商品类型")
enum class GoodsTyping(private val v: Int) : IntTyping {
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
  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?): GoodsTyping? = entries.find { it.value == v }
  }
}

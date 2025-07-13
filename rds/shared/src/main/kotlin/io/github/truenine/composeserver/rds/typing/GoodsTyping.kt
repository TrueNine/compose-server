package io.github.truenine.composeserver.rds.typing

import io.github.truenine.composeserver.IIntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * 商品服务类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Deprecated("无明确业务类型混入")
@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsTyping(v: Int) : IIntTyping {
  /** 实体商品 */
  @EnumItem(ordinal = 1) PHYSICAL_GOODS(1),

  /** 服务商品 */
  @EnumItem(ordinal = 2) SERVICE_GOODS(2),

  /** 虚拟商品 */
  @EnumItem(ordinal = 3) VIRTUAL_GOODS(3);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?): GoodsTyping? = entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?): GoodsTyping? = findVal(v)
  }
}

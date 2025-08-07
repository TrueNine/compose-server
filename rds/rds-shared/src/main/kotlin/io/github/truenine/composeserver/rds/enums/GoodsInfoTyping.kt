package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** 商品信息分类 */
@Deprecated("无明确业务类型混入")
@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsInfoTyping(v: Int) : IIntEnum {
  /** 检索类型 */
  @EnumItem(ordinal = 1) RETRIEVAL(1),

  /** 商品单位信息 */
  @EnumItem(ordinal = 2) GOODS_UNIT_INFO(2),

  /** 商品单位继承信息 */
  @EnumItem(ordinal = 3) GOODS_UNIT_EXTEND_INFO(3);

  override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

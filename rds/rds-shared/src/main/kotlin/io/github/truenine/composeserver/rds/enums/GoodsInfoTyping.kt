package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** Goods information categories */
@Deprecated("Mixed with unclear business types")
@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsInfoTyping(v: Int) : IIntEnum {
  /** Retrieval type */
  @EnumItem(ordinal = 1) RETRIEVAL(1),

  /** Goods unit information */
  @EnumItem(ordinal = 2) GOODS_UNIT_INFO(2),

  /** Goods unit inheritance information */
  @EnumItem(ordinal = 3) GOODS_UNIT_EXTEND_INFO(3);

  override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

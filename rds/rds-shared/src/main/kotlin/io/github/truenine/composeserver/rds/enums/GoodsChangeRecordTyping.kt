package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * Goods change types
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Deprecated("Mixed with unclear business types")
@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsChangeRecordTyping(v: Int) : IIntEnum {
  /** No change */
  @EnumItem(ordinal = 0) NONE(0),

  /** Price changed */
  @EnumItem(ordinal = 1) CHANGE_PRICE(1),

  /** Title changed */
  @EnumItem(ordinal = 2) CHANGE_TITLE(2);

  override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * Goods and service types
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Deprecated("Mixed with unclear business types")
@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsTyping(v: Int) : IIntEnum {
  /** Physical goods */
  @EnumItem(ordinal = 1) PHYSICAL_GOODS(1),

  /** Service goods */
  @EnumItem(ordinal = 2) SERVICE_GOODS(2),

  /** Virtual goods */
  @EnumItem(ordinal = 3) VIRTUAL_GOODS(3);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?): GoodsTyping? = entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?): GoodsTyping? = findVal(v)
  }
}

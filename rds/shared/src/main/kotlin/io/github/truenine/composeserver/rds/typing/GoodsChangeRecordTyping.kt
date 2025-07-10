package io.github.truenine.composeserver.rds.typing

import io.github.truenine.composeserver.typing.IntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * 商品改动类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Deprecated("不明确的业务类型混入")
@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsChangeRecordTyping(v: Int) : IntTyping {
  /** 无改动 */
  @EnumItem(ordinal = 0) NONE(0),

  /** 改价格 */
  @EnumItem(ordinal = 1) CHANGE_PRICE(1),

  /** 改标题 */
  @EnumItem(ordinal = 2) CHANGE_TITLE(2);

  override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

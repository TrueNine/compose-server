package net.yan100.compose.rds.core.typing

import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.rds.core.typing.RuleTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** ## 规则状态 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class RuleTyping(v: Int) : IntTyping {
  /** 无类型 */
  NONE(0),

  /** 排除 */
  @EnumItem(ordinal = 1) EXCLUDE(1),

  /** 包含 */
  @EnumItem(ordinal = 2) INCLUDE(2),

  /** 固定 */
  @EnumItem(ordinal = 3) FIXED(3);

  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(e: Int?): RuleTyping? = entries.find { it.value == e }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}

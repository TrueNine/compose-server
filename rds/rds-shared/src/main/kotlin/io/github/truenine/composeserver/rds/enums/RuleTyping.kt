package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** ## Rule status */
@EnumType(EnumType.Strategy.ORDINAL)
enum class RuleTyping(v: Int) : IIntEnum {
  /** No type */
  NONE(0),

  /** Exclude */
  @EnumItem(ordinal = 1) EXCLUDE(1),

  /** Include */
  @EnumItem(ordinal = 2) INCLUDE(2),

  /** Fixed */
  @EnumItem(ordinal = 3) FIXED(3);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(e: Int?): RuleTyping? = entries.find { it.value == e }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}

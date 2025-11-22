package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** Relation type */
@EnumType(EnumType.Strategy.ORDINAL)
enum class RelationTyping(v: Int) : IIntEnum {
  /** None */
  @EnumItem(ordinal = 0) NONE(0),

  /** Victim */
  @EnumItem(ordinal = 1) BENEFICIARIES(1),

  /** Accomplice */
  @EnumItem(ordinal = 2) PARTICIPATOR(2),

  /** Witness */
  @EnumItem(ordinal = 3) WITNESS(3),

  /** Other */
  @EnumItem(ordinal = 9999) OTHER(9999);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}

package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** Related object type */
@EnumType(EnumType.Strategy.ORDINAL)
enum class RelationItemTyping(v: Int) : IIntEnum {
  /** None */
  @EnumItem(ordinal = 0) NONE(0),

  /** User */
  @EnumItem(ordinal = 1) USER(1),

  /** Customer */
  @EnumItem(ordinal = 2) CUSTOMER(2),

  /** Enterprise */
  @EnumItem(ordinal = 3) ENTERPRISE(3),

  /** Employee */
  @EnumItem(ordinal = 4) EMPLOYEE(4),

  /** Other */
  @EnumItem(ordinal = 9999) OTHER(9999);

  override val value: Int = v

  companion object {
    fun findVal(v: Int?) = entries.find { it.value == v }
  }
}

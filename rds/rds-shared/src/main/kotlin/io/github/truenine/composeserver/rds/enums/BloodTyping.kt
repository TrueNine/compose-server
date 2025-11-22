package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** ## Common blood type of user information */
@EnumType(EnumType.Strategy.ORDINAL)
enum class BloodTyping(v: Int) : IIntEnum {
  /** Type A */
  @EnumItem(ordinal = 1) A(1),

  /** Type B */
  @EnumItem(ordinal = 2) B(2),

  /** Type AB */
  @EnumItem(ordinal = 3) AB(3),

  /** Type O */
  @EnumItem(ordinal = 4) O(4),

  /** Other blood type */
  @EnumItem(ordinal = 9999) OTHER(9999);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = BloodTyping.entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** User education level */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DegreeTyping(v: Int, val level: Int) : IIntEnum {
  /** Illiterate */
  @EnumItem(ordinal = 0) NONE(0, 0),

  /** Primary school */
  @EnumItem(ordinal = 1) MIN(1, 1),

  /** Junior high school */
  @EnumItem(ordinal = 2) HALF(2, 2),

  /** Secondary vocational school */
  @EnumItem(ordinal = 8) HALF_TECH(8, 3),

  /** High school */
  @EnumItem(ordinal = 3) HEIGHT(3, 4),

  /** Junior college */
  @EnumItem(ordinal = 9) HEIGHT_TECH(9, 5),

  /** Bachelor degree */
  @EnumItem(ordinal = 4) BIG(4, 6),

  /** Postgraduate */
  @EnumItem(ordinal = 5) DISCOVERY(5, 7),

  /** Doctorate */
  @EnumItem(ordinal = 6) EXPERT(6, 8),

  /** Postdoctoral */
  @EnumItem(ordinal = 7) AFTER_EXPERT(7, 9),

  /** Other */
  @EnumItem(ordinal = 9999) OTHER(9999, -1);

  override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = DegreeTyping.entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int) = findVal(v)
  }
}

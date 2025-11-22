package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * Disability categories for second- and third-generation Chinese disability certificates.
 *
 * @author TrueNine
 * @since 2023-11-03
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DisTyping(typ: Int) : IIntEnum {
  /** Visual impairment */
  @EnumItem(ordinal = 1) EYE(1),

  /** Hearing impairment */
  @EnumItem(ordinal = 2) EAR(2),

  /** Speech impairment */
  @EnumItem(ordinal = 3) MOUTH(3),

  /** Physical impairment */
  @EnumItem(ordinal = 4) BODY(4),

  /** Intellectual disability */
  @EnumItem(ordinal = 5) IQ(5),

  /** Mental disorder */
  @EnumItem(ordinal = 6) NERVE(6),

  /** Multiple disabilities */
  @EnumItem(ordinal = 7) MULTIPLE(7);

  override val value = typ

  companion object {
    @JvmStatic fun findVal(typ: Int?) = entries.find { it.value == typ }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}

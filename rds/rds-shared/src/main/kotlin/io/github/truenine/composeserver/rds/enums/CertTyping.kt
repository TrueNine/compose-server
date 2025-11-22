package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** ## Certificate types */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertTyping(v: Int) : IIntEnum {
  /** No specific type */
  @EnumItem(ordinal = 0) NONE(0),

  /** Identity card */
  @EnumItem(ordinal = 1) @Deprecated("Use an explicit identity card type") ID_CARD(1),

  /** Second-generation identity card */
  @EnumItem(ordinal = 2) ID_CARD2(2),

  /** Disability certificate */
  @EnumItem(ordinal = 3) @Deprecated("Use an explicit disability certificate type") DISABILITY_CARD(3),

  /** Second-generation disability certificate */
  @EnumItem(ordinal = 4) DISABILITY_CARD2(4),

  /** Third-generation disability card */
  @EnumItem(ordinal = 5) DISABILITY_CARD3(5),

  /** Household registration document */
  @EnumItem(ordinal = 6) HOUSEHOLD_CARD(6),

  /** Bank card */
  @EnumItem(ordinal = 7) BANK_CARD(7),

  /** Contract */
  @EnumItem(ordinal = 8) CONTRACT(8),

  /** Business license */
  @EnumItem(ordinal = 9) BIZ_LICENSE(9),

  /** Portrait photo */
  @EnumItem(ordinal = 10) TITLE_IMAGE(10),

  /** Personal income tax status screen recording */
  @EnumItem(ordinal = 11) PERSONAL_INCOME_TAX_VIDEO(11);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = CertTyping.entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

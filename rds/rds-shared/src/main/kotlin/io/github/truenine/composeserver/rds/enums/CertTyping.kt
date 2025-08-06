package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** ## 证件种类 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertTyping(v: Int) : IIntEnum {
  /** 无具体类型 */
  @EnumItem(ordinal = 0) NONE(0),

  /** 身份证 */
  @EnumItem(ordinal = 1) @Deprecated("使用明确的身份证") ID_CARD(1),

  /** 二代身份证 */
  @EnumItem(ordinal = 2) ID_CARD2(2),

  /** 残疾证 */
  @EnumItem(ordinal = 3) @Deprecated("使用明确的残疾证") DISABILITY_CARD(3),

  /** 二代残疾证 */
  @EnumItem(ordinal = 4) DISABILITY_CARD2(4),

  /** 三代残疾卡 */
  @EnumItem(ordinal = 5) DISABILITY_CARD3(5),

  /** 户口 */
  @EnumItem(ordinal = 6) HOUSEHOLD_CARD(6),

  /** 银行卡 */
  @EnumItem(ordinal = 7) BANK_CARD(7),

  /** 合同 */
  @EnumItem(ordinal = 8) CONTRACT(8),

  /** 营业执照 */
  @EnumItem(ordinal = 9) BIZ_LICENSE(9),

  /** 寸照 */
  @EnumItem(ordinal = 10) TITLE_IMAGE(10),

  /** 个人所得税状况录屏 */
  @EnumItem(ordinal = 11) PERSONAL_INCOME_TAX_VIDEO(11);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = CertTyping.entries.find { it.value == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}

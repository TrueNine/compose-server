package io.github.truenine.composeserver.enums

import io.github.truenine.composeserver.IStringEnum

/**
 * ## ISO 4217 enumeration for different country currencies
 *
 * See also: [Wikipedia](https://en.wikipedia.org/wiki/ISO_4217)
 *
 * @author TrueNine
 * @since 2023-05-28
 */
enum class ISO4217(private val iso4217Str: String, private val cnDescription: String, private val numCode: Int, private val helperCode: Int) : IStringEnum {
  /** ## China Yuan Renminbi */
  CNY("CNY", "人民币", 156, 2),

  /** ## Hong Kong Dollar */
  HKD("HKD", "港元", 344, 2),

  /** ## Macao Pataca */
  MOP("MOP", "澳门币", 446, 2),

  /** ## New Taiwan Dollar */
  TWD("TWD", "新台币", 901, 2),

  /** ## Euro */
  EUR("EUR", "欧元", 978, 2),

  /** ## US Dollar */
  USD("USD", "美元", 840, 2),

  /** ## Great British Pound */
  GBP("GBP", "英镑", 826, 2),

  /** ## Japanese Yen */
  JPY("JPY", "日元", 392, 2),

  /** ## South Korean Won */
  KRW("KRW", "韩圆", 410, 0);

  // @JsonValue
  override val value: String = this.iso4217Str

  companion object {
    @JvmStatic
    operator fun get(v: String?): ISO4217? {
      if (v == null) return null
      var r = entries.find { it.iso4217Str == v }
      if (r == null) r = findValByDesc(v)
      return r
    }

    @JvmStatic
    fun findValByDesc(v: String?): ISO4217? {
      return entries.find { it.cnDescription == v }
    }
  }

  override fun toString(): String {
    return this.iso4217Str
  }
}

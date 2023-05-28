package net.yan100.compose.core.lang

/**
 * ## ISO 4217 表示各国货币的枚举
 * @author TrueNine
 * @since 2023-05-28
 */
enum class ISO4217(
  private val iso4217Str: String
) : StringTyping {
  /**
   * ## 人民币
   * China Yuan Renminbi
   */
  CNY("CNY"),

  /**
   * ## 港元
   * Hong Kong Dollar
   */
  HKD("HKD"),

  /**
   * ## 新台币
   * New Taiwan Dollar
   */
  TWD("TWD"),

  /**
   * ## 欧元
   * Euro
   */
  EUR("EUR"),

  /**
   * ## 美元
   * US Dollar
   */
  USD("USD"),

  /**
   * ## 英镑
   * Great British Pound
   */
  GBP("GBP"),

  /**
   * ## 日元
   * Japanese Yen
   */
  JPY("JPY");

  override fun getValue(): String? {
    return this.iso4217Str
  }

  override fun toString(): String {
    return this.iso4217Str
  }
}

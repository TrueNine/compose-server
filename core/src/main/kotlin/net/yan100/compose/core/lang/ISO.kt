/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.lang

import com.fasterxml.jackson.annotation.JsonValue

/**
 * ## ISO 4217 表示各国货币的枚举
 *
 * 另请参考：[维基百科](https://zh.wikipedia.org/wiki/ISO_4217)
 *
 * @author TrueNine
 * @since 2023-05-28
 */
enum class ISO4217(
  private val iso4217Str: String,
  private val cnDescription: String,
  private val numCode: Int,
  private val helperCode: Int
) : StringTyping {
  /**
   * ## 人民币
   * China Yuan Renminbi
   */
  CNY("CNY", "人民币", 156, 2),

  /**
   * ## 港元
   * Hong Kong Dollar
   */
  HKD("HKD", "港元", 344, 2),

  /**
   * ## 澳门币
   * Macao Pataca
   */
  MOP("MOP", "澳门币", 446, 2),

  /**
   * ## 新台币
   * New Taiwan Dollar
   */
  TWD("TWD", "新台币", 901, 2),

  /**
   * ## 欧元
   * Euro
   */
  EUR("EUR", "欧元", 978, 2),

  /**
   * ## 美元
   * US Dollar
   */
  USD("USD", "美元", 840, 2),

  /**
   * ## 英镑
   * Great British Pound
   */
  GBP("GBP", "英镑", 826, 2),

  /**
   * ## 日元
   * Japanese Yen
   */
  JPY("JPY", "日元", 392, 2),

  /**
   * ## 韩圆
   * South Korean Won
   */
  KRW("KRW", "韩圆", 410, 0);

  @JsonValue override val value: String = this.iso4217Str

  companion object {
    @JvmStatic
    fun findVal(v: String?): ISO4217? {
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

typealias ISO4217Typing = ISO4217

typealias Currency = ISO4217

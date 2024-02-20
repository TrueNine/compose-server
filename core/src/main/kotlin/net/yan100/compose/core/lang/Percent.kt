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

import java.math.BigDecimal

/**
 * # 百分比计算工具
 *
 * @author TrueNine
 * @since 2023-05-16
 */
object Percent {
  private val HUNDRED = BigDecimal("100")

  /**
   * ## 将百分比数值转换为小数
   *
   * ```kotlin
   *     return percent / BigDecimal("100")
   * ```
   *
   * @return 小数点位数
   */
  @JvmStatic
  fun mix(percent: BigDecimal): BigDecimal {
    return percent.multiply(HUNDRED) / HUNDRED
  }

  @JvmStatic
  fun lazyToMix(percent: () -> BigDecimal): BigDecimal {
    return mix(percent())
  }

  /**
   * # 将小数点转换为百分比
   *
   * ```kotlin
   *     return mix * BigDecimal("100")
   * ```
   */
  @JvmStatic
  fun toPercent(mix: BigDecimal): BigDecimal {
    return mix * HUNDRED
  }

  @JvmStatic
  fun lazyToPercent(mix: () -> BigDecimal): BigDecimal {
    return toPercent(mix())
  }
}

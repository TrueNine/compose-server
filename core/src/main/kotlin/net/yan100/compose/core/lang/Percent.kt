package net.yan100.compose.core.lang

import java.math.BigDecimal

/**
 * # 百分比计算工具
 * @author TrueNine
 * @since 2023-05-16
 */
object Percent {
    private val HUNDRED = BigDecimal("100")

    /**
     * ## 将百分比数值转换为小数
     * ```kotlin
     *     return percent / BigDecimal("100")
     * ```
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

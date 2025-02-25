package net.yan100.compose.core

/**
 * ## 安全地转换为 Int
 *
 * 如果超出范围，则返回 Int.MAX_VALUE 或 Int.MIN_VALUE 返回
 */
fun Long.toSafeInt(): Int {
  return if (this > Int.MAX_VALUE) Int.MAX_VALUE
  else if (this < Int.MIN_VALUE) Int.MIN_VALUE else toInt()
}

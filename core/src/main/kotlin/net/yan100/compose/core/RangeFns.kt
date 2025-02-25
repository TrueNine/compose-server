package net.yan100.compose.core

fun LongRange.toSafeRange(min: Long = 0, max: Long = this.last): LongRange {
  val n = if (this.first < min) min else this.first
  val x = if (this.last > max) max else this.last
  return LongRange(n, x)
}

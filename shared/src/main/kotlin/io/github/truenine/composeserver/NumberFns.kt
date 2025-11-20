package io.github.truenine.composeserver

/**
 * ## Safely convert Long to Int.
 *
 * If the value is out of Int range, returns Int.MAX_VALUE or Int.MIN_VALUE.
 */
fun Long.toSafeInt(): Int {
  return if (this > Int.MAX_VALUE) Int.MAX_VALUE else if (this < Int.MIN_VALUE) Int.MIN_VALUE else toInt()
}

package io.github.truenine.composeserver

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

fun <E> mutableLockListOf(vararg elements: E): List<E> {
  return CopyOnWriteArrayList(elements)
}

fun <E> mutableLockListOf(): List<E> {
  return CopyOnWriteArrayList()
}

/** @see kotlin.collections.mapCapacity */
private fun mapCapacity(expectedSize: Int): Int =
  when {
    expectedSize < 0 -> expectedSize
    expectedSize < 3 -> expectedSize + 1
    expectedSize < INT_MAX_POWER_OF_TWO -> ((expectedSize / 0.75F) + 1.0F).toInt()

    else -> Int.MAX_VALUE
  }

private const val INT_MAX_POWER_OF_TWO: Int = 1 shl (Int.SIZE_BITS - 2)

fun <K, v> mutableLockMapOf(vararg pairs: Pair<K, v>): MutableMap<K, v> {
  return ConcurrentHashMap<K, v>(mapCapacity(pairs.size)).apply { putAll(pairs) }
}

fun <K, v> mutableLockMapOf(): MutableMap<K, v> {
  return ConcurrentHashMap<K, v>()
}

fun <T, C : Collection<T>, R> C?.isNotEmptyRun(block: C.() -> R): R? {
  return if (!this.isNullOrEmpty()) block(this) else null
}

/**
 * ## 将 [Pair] 派生为 [Triple]
 *
 * @see [Triple]
 */
infix fun <A, B, C> Pair<A, B>.and(other: C): Triple<A, B, C> {
  return Triple(this.first, this.second, other)
}

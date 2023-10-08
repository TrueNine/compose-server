package net.yan100.compose.core.lang

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

fun <E> mutableLockListOf(vararg elements: E): List<E> {
  return CopyOnWriteArrayList(elements)
}

fun <E> mutableLockListOf(): List<E> {
  return CopyOnWriteArrayList()
}

/**
 * @see kotlin.collections.mapCapacity
 */
fun mapCapacity(expectedSize: Int): Int = when {
  expectedSize < 0 -> expectedSize
  expectedSize < 3 -> expectedSize + 1
  expectedSize < INT_MAX_POWER_OF_TWO -> ((expectedSize / 0.75F) + 1.0F).toInt()
  else -> Int.MAX_VALUE
}

private const val INT_MAX_POWER_OF_TWO: Int = 1 shl (Int.SIZE_BITS - 2)


fun <K, v> mutableLockMapOf(vararg pairs: Pair<K, v>): Map<K, v> {
  return ConcurrentHashMap<K, v>(mapCapacity(pairs.size)).apply { putAll(pairs) }
}

fun <K, v> mutableLockMapOf(): Map<K, v> {
  return ConcurrentHashMap<K, v>()
}

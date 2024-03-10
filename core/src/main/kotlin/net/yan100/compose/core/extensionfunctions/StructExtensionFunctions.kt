/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.extensionfunctions

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

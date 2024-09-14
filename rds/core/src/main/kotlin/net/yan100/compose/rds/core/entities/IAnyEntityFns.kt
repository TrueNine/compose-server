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
package net.yan100.compose.rds.core.entities

/** 将自身置空为新的 Entity 对象 */
fun <T : IAnyEntity> T.withNew(): T {
  toNewEntity()
  return this
}

inline fun <T : IAnyEntity> T.withNew(crossinline after: (T) -> T): T = after(withNew())

/** 将集合内的所有元素置空为新的 Entity 对象 */
fun <T : IAnyEntity> List<T>.withNew(): List<T> = map { it.withNew() }

inline fun <T : IAnyEntity> List<T>.withNew(crossinline after: (List<T>) -> List<T>): List<T> = after(this.map { it.withNew() })

inline fun <T : IAnyEntity, R : Any> List<T>.withNewMap(crossinline after: (List<T>) -> List<R>): List<R> = after(this.withNew())

/** ## 判断当前实体是否为新实体，然后执行 update */
inline fun <T : IAnyEntity> T.takeUpdate(throwException: Boolean = true, crossinline after: (T) -> T?): T? {
  if (!isNew) return after(this) else if (throwException) throw IllegalStateException("当前数据为新数据，不能执行更改")
  return null
}

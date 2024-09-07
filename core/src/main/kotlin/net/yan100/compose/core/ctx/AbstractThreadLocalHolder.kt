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
package net.yan100.compose.core.ctx

import org.springframework.core.NamedInheritableThreadLocal
import java.io.Closeable
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractThreadLocalHolder<T>(nameId: KClass<*>? = null, defaultValue: T? = null) : Closeable {
  private val holder by lazy {
    val name = nameId?.qualifiedName ?: this::class.qualifiedName ?: UUID.randomUUID().toString()
    NamedInheritableThreadLocal<T>(name)
  }

  init {
    if (defaultValue != null) holder.set(defaultValue)
  }

  override fun close() = holder.remove()

  var content: T
    get() = holder.get()
    set(value) = holder.set(value)

  open fun get(): T = holder.get()

  open fun set(value: T) = holder.set(value)

  fun component1(): T = holder.get()

  fun plusAssign(value: T) = holder.set(value)
}

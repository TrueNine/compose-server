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
package net.yan100.compose.core

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.core.ctx.ObjectMapperHolder
import java.io.Serializable
import kotlin.reflect.KClass

inline fun <reified T : Any> ITypedValue.toTypedValue(): T? {
  return toTypedValue(T::class)
}

interface ITypedValue : Serializable {
  @get:JsonIgnore
  @get:Transient
  val typedSerialValue: Any?
    get() = null

  @JsonIgnore
  @Transient
  fun <T : Any> toTypedValue(type: KClass<out T>): T? {
    val map = ObjectMapperHolder.get()
    return map.readValue(typedSerialValue?.toString() ?: "null", type.java)
  }
}

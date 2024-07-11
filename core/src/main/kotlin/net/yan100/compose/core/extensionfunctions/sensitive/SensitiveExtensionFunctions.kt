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
package net.yan100.compose.core.extensionfunctions.sensitive

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import net.yan100.compose.core.annotations.SensitiveRef

interface SensitiveScope<T> {
  fun String.addressDetails() = SensitiveRef.Strategy.ADDRESS.desensitizeSerializer()(this)

  fun String.bankCard() = SensitiveRef.Strategy.BANK_CARD_CODE.desensitizeSerializer()(this)

  fun String.chinaName() = SensitiveRef.Strategy.NAME.desensitizeSerializer()(this)

  fun String.multipleName() = SensitiveRef.Strategy.MULTIPLE_NAME.desensitizeSerializer()(this)

  fun String.chinaIdCard() = SensitiveRef.Strategy.ID_CARD.desensitizeSerializer()(this)

  fun String.chinaPhone() = SensitiveRef.Strategy.PHONE.desensitizeSerializer()(this)

  fun String.password() = SensitiveRef.Strategy.PASSWORD.desensitizeSerializer()(this)

  fun String.email() = SensitiveRef.Strategy.EMAIL.desensitizeSerializer()(this)

  fun String.once() = SensitiveRef.Strategy.ONCE.desensitizeSerializer()(this)
}

@OptIn(ExperimentalContracts::class)
inline fun <T> sensitiveAlso(data: T, scope: SensitiveScope<T>.(data: T) -> Unit): T {
  contract { callsInPlace(scope, InvocationKind.EXACTLY_ONCE) }
  scope(object : SensitiveScope<T> {}, data)
  return data
}

@OptIn(ExperimentalContracts::class)
inline fun <T> sensitiveLet(data: T, scope: SensitiveScope<T>.(data: T) -> T): T {
  contract { callsInPlace(scope, InvocationKind.EXACTLY_ONCE) }
  return scope(object : SensitiveScope<T> {}, data)
}

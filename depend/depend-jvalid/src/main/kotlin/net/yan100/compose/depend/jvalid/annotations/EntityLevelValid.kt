/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.depend.jvalid.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass
import net.yan100.compose.depend.jvalid.AnyNilValidator

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Constraint(validatedBy = [AnyNilValidator::class])
annotation class EntityLevelValid(
  val message: String = "数据未通过校验",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)
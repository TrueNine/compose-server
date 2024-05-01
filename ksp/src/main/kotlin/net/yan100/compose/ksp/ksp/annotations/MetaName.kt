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
package net.yan100.compose.ksp.ksp.annotations

import net.yan100.compose.core.extensionfunctions.hasText
import java.lang.annotation.Inherited

private typealias tg = AnnotationTarget

@MustBeDocumented
@Repeatable
@Inherited
@Target(tg.FUNCTION, tg.TYPE, tg.CLASS, tg.FIELD, tg.PROPERTY_GETTER, tg.PROPERTY_SETTER)
@Retention(AnnotationRetention.BINARY)
annotation class MetaName(val value: String = "", val name: String = "")

fun Sequence<MetaName>.getFirstName(): String? {
  val value = this.firstOrNull()?.name
  val name = this.firstOrNull()?.value
  return if (value.hasText()) value
  else if (name.hasText()) name
  else null
}

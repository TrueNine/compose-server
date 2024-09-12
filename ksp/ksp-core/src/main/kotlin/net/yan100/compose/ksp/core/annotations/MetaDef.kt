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
package net.yan100.compose.ksp.core.annotations

import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@MustBeDocumented
@Repeatable
@Inherited
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.CLASS,
  AnnotationTarget.FIELD,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class MetaDef(
  /**
   * ## 是否为副作用对象
   *
   * 例如：jpa 同一个表，但实体不同，则需要将另一个实体所有字段设定为不更新，仅为查询对象
   */
  val shadow: Boolean = false,

  /**
   * ## 扩展超类，或额外指定的类型
   *
   * 例如：在 jpa 当中，默认继承 [net.yan100.compose.rds.core.entities.IEntity]， 但如果需要继承别的类，则可以单独指定，例如：[net.yan100.compose.rds.core.entities.ITreeEntity]
   */
  val extendBy: KClass<*> = Unit::class
)

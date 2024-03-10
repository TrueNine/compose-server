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
package net.yan100.compose.core.annotations

import java.lang.annotation.Inherited
import org.jetbrains.annotations.ApiStatus

/**
 * 该注解标志这这个类、方法、参数…… <br></br> 可能在未来版本移除或者变更，因为底层依赖不稳定 <br></br> 或者目前也没有找到好的解决方式
 *
 * @author TrueNine
 * @since 2022-10-26
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.ANNOTATION_CLASS,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.FIELD,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.CLASS
)
@MustBeDocumented
@ApiStatus.Experimental
annotation class BetaTest

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

private typealias atg = AnnotationTarget

/**
 * # 可见性
 *
 * 设定元数据的可见性
 *
 * 例如：
 * - 给前端暴露的 val 序列化字段
 * - 不需要脱敏的字段
 * - 需要可见性为公开的字段
 * - 不需要密封的接口
 *
 * 但请注意，此类仅控制其字段的可见性，并不直接控制字段的生成， 在绝大多数情况下，字段会按环境要求，注解例如 [kotlin.jvm.Transient],[jakarta.persistence.Transient]等注解
 */
@MustBeDocumented
@Repeatable
@Inherited
@Target(atg.FUNCTION, atg.TYPE, atg.CLASS, atg.FIELD, atg.PROPERTY_GETTER, atg.PROPERTY_SETTER)
@Retention(AnnotationRetention.BINARY)
annotation class MetaVisible(
  /** ## 控制其可见性，效果等同于 反向[hidden] */
  val value: Boolean = true,
  /** ## 注释其可写性 */
  val writeOnly: Boolean = false,
  /** ## 注释其可读性 */
  val readOnly: Boolean = false,
  /** ## 是否进行隐藏 */
  val hidden: Boolean = false
)

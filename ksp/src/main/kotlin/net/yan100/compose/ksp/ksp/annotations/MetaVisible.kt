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
 */
@MustBeDocumented
@Repeatable
@Inherited
@Target(atg.FUNCTION, atg.TYPE, atg.CLASS, atg.FIELD, atg.PROPERTY_GETTER, atg.PROPERTY_SETTER)
@Retention(AnnotationRetention.BINARY)
annotation class MetaVisible(val value: Boolean = true)

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
package net.yan100.compose.core.annotations

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.lang.annotation.Inherited
import net.yan100.compose.core.jackson.LongAsStringSerializer
import net.yan100.compose.core.jackson.StringAsLongDeserializer

/**
 * 将 Long 类型序列化为 String，同样反序列化回来也采取 String
 *
 * @author TrueNine
 * @since 2023-02-19
 */
@Inherited
@JsonInclude
@MustBeDocumented
@JacksonAnnotationsInside
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@JsonSerialize(using = LongAsStringSerializer::class)
@JsonDeserialize(using = StringAsLongDeserializer::class)
annotation class BigIntegerAsString
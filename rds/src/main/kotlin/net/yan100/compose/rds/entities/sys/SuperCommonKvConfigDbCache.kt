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
package net.yan100.compose.rds.entities.sys

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.ITypedValue
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@Schema(title = "通用配置缓存")
@MappedSuperclass
abstract class SuperCommonKvConfigDbCache : IEntity(), ITypedValue {
  /** ## 配置 key */
  @get:Pattern(regexp = Regexes.CONFIG_KEY) @get:Schema(title = "配置 key") abstract var k: String

  /** ## 配置 json value */
  @get:Hidden @get:JsonIgnore @get:Schema(title = "配置 value") abstract var v: String?

  override val typedSerialValue: Any?
    get() = v
}

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
package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.core.RefId
import net.yan100.compose.core.domain.Coordinate
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.crud.converters.WGS84Converter

@MetaDef
interface SuperAddressDetails : IJpaEntity {
  /** 地址 id */
  var addressId: RefId

  /** 联系电话 */
  var phone: string?

  /** ## 用户 id */
  var userId: RefId

  /** 联系人名称 */
  var name: String?

  /** 地址代码 */
  var addressCode: string

  /** 地址详情 */
  var addressDetails: String

  /** 定位 */
  @get:Convert(converter = WGS84Converter::class)
  var center: Coordinate?
}

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
  @get:Convert(converter = WGS84Converter::class) var center: Coordinate?
}

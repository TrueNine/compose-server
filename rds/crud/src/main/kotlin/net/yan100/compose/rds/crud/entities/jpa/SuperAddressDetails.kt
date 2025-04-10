package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.RefId
import net.yan100.compose.domain.Coordinate
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.WGS84Converter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.string

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

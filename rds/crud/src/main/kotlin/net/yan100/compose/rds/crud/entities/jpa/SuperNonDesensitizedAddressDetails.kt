package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef

@MetaDef(shadow = true)
interface SuperNonDesensitizedAddressDetails : SuperAddressDetails {
  /** 联系电话 */
  override var phone: String?

  /** 地址详情 */
  override var addressDetails: String

  /** 地址代码 */
  override var addressCode: string

  /** 联系人名称 */
  override var name: String?
}

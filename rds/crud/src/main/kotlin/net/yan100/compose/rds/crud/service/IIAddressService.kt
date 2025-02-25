package net.yan100.compose.rds.crud.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.Address

/**
 * # 地址服务
 *
 * @author TrueNine
 * @since 2023-05-08
 */
interface IIAddressService : ICrud<Address> {
  fun findRoot(): Address

  fun initProvince()

  fun findProvinces(): List<Address>

  fun findDirectChildrenByCode(code: String): List<Address>

  fun findFullPathById(id: String): String
}

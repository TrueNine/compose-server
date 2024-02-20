package net.yan100.compose.rds.service

import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.service.base.IService

interface IAddressService : IService<Address> {
  fun findRoot(): Address
  fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address>
  fun findProvinces(): List<Address>
  fun findByCodeAndLevel(code: SerialCode, level: Int): Address?
  fun findDirectChildrenByCode(code: String): List<Address>
  fun findDirectChildrenById(id: String): List<Address>
  fun findFullPathById(id: RefId): String
  fun findFullPathByCode(code: RefId): String
  fun findAllByCodeIn(codes: List<String>): List<Address>
}

package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.service.base.IService

interface IAddressService : IService<Address> {
  fun findRoot(): Address
  fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address>
  fun findProvinces(): List<Address>
  fun findDirectChildrenByCode(code: String): List<Address>
  fun findDirectChildrenById(id: String): List<Address>
  fun findFullPathById(id: String): String
  fun findFullPathByCode(id: String): String
  fun findAllByCodeIn(codes: List<String>): List<Address>
}

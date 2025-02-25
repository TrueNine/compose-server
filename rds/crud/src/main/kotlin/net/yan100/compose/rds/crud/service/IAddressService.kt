package net.yan100.compose.rds.crud.service

import net.yan100.compose.core.RefId
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.Address

interface IAddressService : ICrud<Address> {
  fun findByCode(code: string): Address?

  fun findRoot(): Address

  fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address>

  fun findProvinces(): List<Address>

  fun findByCodeAndLevel(code: string, level: Int): Address?

  fun findDirectChildrenByCode(code: String): List<Address>

  fun findDirectChildrenById(id: RefId): List<Address>

  fun findFullPathById(id: RefId): String

  fun findFullPathByCode(code: RefId): String

  fun findAllByCodeIn(codes: List<String>): List<Address>
}

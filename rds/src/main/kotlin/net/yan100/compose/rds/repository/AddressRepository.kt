package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.TreeRepository
import net.yan100.compose.rds.entity.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : TreeRepository<Address> {

  fun findAllByCode(code: String): List<Address>

  fun findByCode(code: String): Address?

  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<Address>
}

package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.TreeRepository
import net.yan100.compose.rds.entity.AddressEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : TreeRepository<AddressEntity> {

  fun findAllByCode(code: String): List<AddressEntity>

  fun findByCode(code: String): AddressEntity?

  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<AddressEntity>
}

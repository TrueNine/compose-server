package com.truenine.component.rds.repository

import com.truenine.component.rds.base.TreeRepository
import com.truenine.component.rds.entity.AddressEntity
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

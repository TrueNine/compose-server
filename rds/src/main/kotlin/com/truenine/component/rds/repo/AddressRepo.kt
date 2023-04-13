package com.truenine.component.rds.repo

import com.truenine.component.rds.base.TreeRepo
import com.truenine.component.rds.entity.AddressEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface AddressRepo : TreeRepo<AddressEntity> {

  fun findAllByCode(code: String): List<AddressEntity>

  fun findByCode(code: String): AddressEntity?

  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<AddressEntity>
}

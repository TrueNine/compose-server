package com.truenine.component.rds.repo

import com.truenine.component.rds.base.PresortTreeRepo
import com.truenine.component.rds.dao.AddressDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface AddressRepo : PresortTreeRepo<AddressDao, String> {

  fun findAllByCode(code: String): List<AddressDao>

  fun findByCode(code: String): AddressDao?

  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<AddressDao>
}

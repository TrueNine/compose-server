package com.truenine.component.rds.repo

import com.truenine.component.rds.base.PreSortTreeRepo
import com.truenine.component.rds.dao.AddressDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface AddressRepo : PreSortTreeRepo<AddressDao, String> {

  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<AddressDao>
}

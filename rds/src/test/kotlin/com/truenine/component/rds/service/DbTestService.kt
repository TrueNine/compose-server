package com.truenine.component.rds.service

import com.truenine.component.rds.repo.DbTestPresortTreeRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class DbTestService(
  private val r: DbTestPresortTreeRepo
) {
  @Transactional
  open fun testTransactional() {

  }
}

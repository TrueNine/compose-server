package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.DbTestBaseServiceEntity
import com.truenine.component.rds.repo.DbTestBaseServiceRepository
import org.springframework.stereotype.Service

@Service
class BaseServiceTester
  (private val repo: DbTestBaseServiceRepository) : BaseServiceImpl<DbTestBaseServiceEntity>(repo) {
}

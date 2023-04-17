package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.DbTestBaseServiceEntity
import com.truenine.component.rds.repository.DbTestBaseServiceRepository
import org.springframework.stereotype.Service

@Service
class BaseServiceTester
  (repo: DbTestBaseServiceRepository) : BaseServiceImpl<DbTestBaseServiceEntity>(repo)

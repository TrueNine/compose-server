package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.DbTestServiceEntity
import net.yan100.compose.rds.repository.DbTestBaseServiceRepository
import org.springframework.stereotype.Service

@Service
class BaseServiceTester
  (repo: DbTestBaseServiceRepository) : BaseServiceImpl<DbTestServiceEntity>(repo)

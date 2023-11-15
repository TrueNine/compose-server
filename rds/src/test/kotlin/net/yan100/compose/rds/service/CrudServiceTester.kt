package net.yan100.compose.rds.service

import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.entities.DbTestServiceEntity
import net.yan100.compose.rds.repositories.DbTestBaseServiceRepository
import org.springframework.stereotype.Service

@Service
class CrudServiceTester
  (repo: DbTestBaseServiceRepository) : CrudService<DbTestServiceEntity>(repo)

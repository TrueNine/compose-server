package net.yan100.compose.rds.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.entities.DbTestMergeTable
import net.yan100.compose.rds.repositories.IDbTestMergeTableRepo
import org.springframework.stereotype.Service

@Service
class DbTestMergeTableServiceImpl(
  private val dRepo: IDbTestMergeTableRepo
) : IDbTestMergeTableService,
  ICrud<DbTestMergeTable> by jpa(dRepo),
  IDbTestMergeTableRepo by dRepo {
}

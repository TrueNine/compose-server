package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.Dept
import net.yan100.compose.rds.repositories.IDeptRepo
import net.yan100.compose.rds.service.IDeptService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class DeptServiceImpl(
  private val repo: IDeptRepo
) : IDeptService, CrudService<Dept>(repo) {

  override fun findAllByUserId(userId: String): List<Dept> {
    return repo.findAllByUserId(userId)
  }
}

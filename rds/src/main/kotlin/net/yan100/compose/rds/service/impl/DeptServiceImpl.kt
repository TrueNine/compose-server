package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.entity.Dept
import net.yan100.compose.rds.repository.DeptRepo
import net.yan100.compose.rds.service.IDeptService
import org.springframework.stereotype.Service

@Service
class DeptServiceImpl(
  private val repo: DeptRepo
) : IDeptService, CrudService<Dept>(repo) {

  override fun findAllByUserId(userId: String): List<Dept> {
    return repo.findAllByUserId(userId)
  }
}

package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.Dept
import net.yan100.compose.rds.repository.DeptRepo
import net.yan100.compose.rds.service.DeptService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service

@Service
class DeptServiceImpl(
  private val repo: DeptRepo
) : DeptService, BaseServiceImpl<Dept>(repo) {

  override fun findAllByUserId(userId: String): List<Dept> {
    return repo.findAllByUserId(userId)
  }
}

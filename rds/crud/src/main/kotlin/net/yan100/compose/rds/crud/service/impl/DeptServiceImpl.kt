package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.Dept
import net.yan100.compose.rds.crud.repositories.jpa.IDeptRepo
import net.yan100.compose.rds.crud.service.IDeptService
import org.springframework.stereotype.Service

@Service
class DeptServiceImpl(private val dRepo: IDeptRepo) :
  IDeptService, ICrud<Dept> by jpa(dRepo) {
  override fun fetchAllByUserId(userId: String): List<Dept> {
    return dRepo.findAllByUserId(userId)
  }
}

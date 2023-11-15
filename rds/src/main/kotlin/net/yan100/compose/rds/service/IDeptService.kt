package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.Dept
import net.yan100.compose.rds.service.base.IService

interface IDeptService : IService<Dept> {
  /**
   * ## 根据用户id查询当前部门
   */
  fun findAllByUserId(userId: String): List<Dept>
}

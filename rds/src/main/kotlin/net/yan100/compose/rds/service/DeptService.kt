package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.Dept

interface DeptService : BaseService<Dept> {
  /**
   * ## 根据用户id查询当前部门
   */
  fun findAllByUserId(userId: String): List<Dept>
}

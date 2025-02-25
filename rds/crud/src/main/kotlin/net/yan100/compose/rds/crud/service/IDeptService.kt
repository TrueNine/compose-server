package net.yan100.compose.rds.crud.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.Dept

interface IDeptService : ICrud<Dept> {
  /** ## 根据用户id查询当前部门 */
  fun fetchAllByUserId(userId: String): List<Dept>
}

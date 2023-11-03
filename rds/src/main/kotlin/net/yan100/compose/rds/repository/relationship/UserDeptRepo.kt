package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.entity.UserDept
import net.yan100.compose.rds.repository.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface UserDeptRepo : IRepo<UserDept> {
  fun findAllByUserId(userId: String): List<UserDept>
}

package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.TreeRepository
import net.yan100.compose.rds.entity.UserDept
import org.springframework.stereotype.Repository

@Repository
interface UserDeptRepo : TreeRepository<UserDept> {
  fun findAllByUserId(userId: String): List<UserDept>
}

package net.yan100.compose.rds.repositories.relationship

import net.yan100.compose.rds.entities.UserDept
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface IUserDeptRepo : IRepo<UserDept> {
  fun findAllByUserId(userId: String): List<UserDept>
}

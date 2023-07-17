package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.UserDept
import org.springframework.stereotype.Repository

@Repository
interface UserDeptRepo : BaseRepository<UserDept> {
  fun findAllByUserId(userId: String): List<UserDept>
}

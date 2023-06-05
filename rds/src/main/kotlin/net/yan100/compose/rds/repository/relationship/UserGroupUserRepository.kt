package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.UserGroupUser
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepository : BaseRepository<UserGroupUser> {
  fun existsByUserGroupIdAndUserId(userId: String, userGroupId: String): Boolean
}

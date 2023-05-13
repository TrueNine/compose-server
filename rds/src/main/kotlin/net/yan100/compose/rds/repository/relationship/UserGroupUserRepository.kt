package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.UserGroupUserEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepository : BaseRepository<UserGroupUserEntity> {
  fun existsByUserGroupIdAndUserId(userId: String, userGroupId: String): Boolean
}

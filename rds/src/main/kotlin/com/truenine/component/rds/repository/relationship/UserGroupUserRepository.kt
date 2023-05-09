package com.truenine.component.rds.repository.relationship

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.relationship.UserGroupUserEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepository : BaseRepository<UserGroupUserEntity> {
  fun existsByUserGroupIdAndUserId(userId: String, userGroupId: String): Boolean
}

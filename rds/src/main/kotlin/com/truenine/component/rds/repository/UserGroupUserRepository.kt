package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserGroupUserEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepository : BaseRepository<UserGroupUserEntity> {
  fun existsByUserGroupIdAndUserId(userId: Long, userGroupId: Long): Boolean
}

package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserGroupUserEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepo : BaseRepo<UserGroupUserEntity> {
  fun existsByUserIdAndUserGroupId(userId: Long, userGroupId: Long): Boolean
}

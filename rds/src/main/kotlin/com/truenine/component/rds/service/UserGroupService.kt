package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.entity.UserGroupEntity

interface UserGroupService : BaseService<UserGroupEntity> {
  fun findAllByLeaderUserId(userId: Long): Set<UserGroupEntity>
  fun findAllByUserAccount(account: String): Set<UserGroupEntity>
  fun saveUserToUserGroup(userId: Long, userGroupId: Long)
}

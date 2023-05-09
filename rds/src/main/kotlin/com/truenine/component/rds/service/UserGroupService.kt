package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.entity.UserGroupEntity

interface UserGroupService : BaseService<UserGroupEntity> {
  fun findAllByLeaderUserId(userId: String): Set<UserGroupEntity>
  fun findAllByUserAccount(account: String): Set<UserGroupEntity>
  fun saveUserToUserGroup(userId: String, userGroupId: String)
}

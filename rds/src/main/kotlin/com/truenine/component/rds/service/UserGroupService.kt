package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.entity.UserGroupEntity

interface UserGroupService : BaseService<UserGroupEntity> {
  fun findAllUserGroupByUserId(userId: Long): Set<UserGroupEntity>
  fun assignUserToUserGroup(userId: Long, userGroupId: Long)
}

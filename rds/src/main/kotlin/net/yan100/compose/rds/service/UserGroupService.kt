package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.UserGroupEntity

interface UserGroupService : BaseService<UserGroupEntity> {
  fun findAllByLeaderUserId(userId: String): Set<UserGroupEntity>
  fun findAllByUserAccount(account: String): Set<UserGroupEntity>
  fun saveUserToUserGroup(userId: String, userGroupId: String)
}

package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.UserGroup

interface UserGroupService : BaseService<UserGroup> {
  fun findAllByLeaderUserId(userId: String): Set<UserGroup>
  fun findAllByUserAccount(account: String): Set<UserGroup>
  fun saveUserToUserGroup(userId: String, userGroupId: String)
}

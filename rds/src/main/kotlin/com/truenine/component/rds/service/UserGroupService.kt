package com.truenine.component.rds.service

import com.truenine.component.rds.entity.UserGroupEntity

interface UserGroupService {
  fun saveUserGroup(userGroup: UserGroupEntity): UserGroupEntity?
  fun deleteUserGroupById(id: String)
  fun findUserGroupById(id: String): UserGroupEntity?
  fun findAllUserGroupByUserId(userId: String): Set<UserGroupEntity>
  fun assignUserToUserGroup(userId: String, userGroupId: String)
}

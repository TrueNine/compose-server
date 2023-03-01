package com.truenine.component.rds.service

import com.truenine.component.rds.dao.UserGroupDao

interface UserGroupService {
  fun saveUserGroup(userGroup: UserGroupDao): UserGroupDao?
  fun deleteUserGroupById(id: String)
  fun findUserGroupById(id: String): UserGroupDao?
  fun findAllUserGroupByUserId(userId: String): Set<UserGroupDao>
  fun assignUserToUserGroup(userId: String, userGroupId: String)
}

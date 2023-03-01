package com.truenine.component.rds.service

import com.truenine.component.rds.dao.*
import com.truenine.component.rds.dto.UserGroupRegisterDto
import com.truenine.component.rds.dto.UserRegisterDto
import com.truenine.component.rds.vo.UsrVo

interface UserAdminService {
  fun registerPlainUser(userRegisterDto: UserRegisterDto?): UserDao?
  fun registerRootUser(rootUserRegisterDto: UserRegisterDto): UserDao?
  fun completionUserInfo(userInfo: UserInfoDao): UserInfoDao?
  fun completionUserInfoByAccount(
    account: String?,
    userInfo: UserInfoDao
  ): UserInfoDao?

  fun updatePasswordByAccountAndOldPassword(
    account: String?,
    oldPwd: String?,
    newPwd: String?
  ): UsrVo?

  fun verifyPassword(account: String?, pwd: String?): Boolean

  fun findUsrVoByAccount(account: String): UsrVo?
  fun findUserById(id: String?): UserDao?
  fun findUserByAccount(account: String): UserDao?

  fun findAllRoleGroupByAccount(account: String): Set<RoleGroupDao>
  fun findAllRoleGroupByUser(user: UserDao): Set<RoleGroupDao>
  fun findAllRoleByAccount(account: String): Set<RoleDao>
  fun findAllRoleByUser(user: UserDao): Set<RoleDao>
  fun findAllPermissionsByAccount(account: String): Set<PermissionsDao>
  fun findAllPermissionsByUser(user: UserDao): Set<PermissionsDao>

  fun revokeRoleGroupByUser(
    user: UserDao,
    roleGroup: RoleGroupDao
  )

  fun registerUserGroup(dto: UserGroupRegisterDto): UserGroupDao?
  fun findAllUserGroupByUser(user: UserDao): Set<UserGroupDao>
  fun deleteUserByAccount(account: String?)
  fun assignUserToUserGroupById(userId: String, userGroupId: String)
}

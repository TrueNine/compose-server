package com.truenine.component.rds.service

import com.truenine.component.rds.entity.*
import com.truenine.component.rds.models.req.PutUserGroupRequestParam
import com.truenine.component.rds.models.req.PutUserRequestParam
import com.truenine.component.rds.models.UserAuthorizationModel

interface UserAdminService {
  fun registerPlainUser(putUserRequestParam: PutUserRequestParam?): UserEntity?
  fun registerRootUser(rootPutUserRequestParam: PutUserRequestParam): UserEntity?
  fun completionUserInfo(userInfo: UserInfoEntity): UserInfoEntity?
  fun completionUserInfoByAccount(
    account: String?,
    userInfo: UserInfoEntity
  ): UserInfoEntity?

  fun updatePasswordByAccountAndOldPassword(
    account: String?,
    oldPwd: String?,
    newPwd: String?
  ): UserAuthorizationModel?

  fun verifyPassword(account: String?, pwd: String?): Boolean

  fun findUserAuthorizationModelByAccount(account: String): UserAuthorizationModel?
  fun findUserById(id: String?): UserEntity?
  fun findUserByAccount(account: String): UserEntity?

  fun findAllRoleGroupByAccount(account: String): Set<RoleGroupEntity>
  fun findAllRoleGroupByUser(user: UserEntity): Set<RoleGroupEntity>
  fun findAllRoleByAccount(account: String): Set<RoleEntity>
  fun findAllRoleByUser(user: UserEntity): Set<RoleEntity>
  fun findAllPermissionsByAccount(account: String): Set<PermissionsEntity>
  fun findAllPermissionsByUser(user: UserEntity): Set<PermissionsEntity>

  fun revokeRoleGroupByUser(
    user: UserEntity,
    roleGroup: RoleGroupEntity
  )

  fun registerUserGroup(dto: PutUserGroupRequestParam): UserGroupEntity?
  fun findAllUserGroupByUser(user: UserEntity): Set<UserGroupEntity>
  fun deleteUserByAccount(account: String?)
  fun assignUserToUserGroupById(userId: String, userGroupId: String)
}

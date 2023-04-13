package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.*
import com.truenine.component.rds.models.UserAuthorizationModel
import com.truenine.component.rds.models.req.PostUserGroupRequestParam
import com.truenine.component.rds.models.req.PostUserRequestParam

interface UserAdminService {
  fun registerPlainUser(userReq: PostUserRequestParam): UserEntity?
  fun registerRootUser(rootPostUserRequestParam: PostUserRequestParam): UserEntity?
  fun completionUserInfo(userInfo: UserInfoEntity): UserInfoEntity?
  fun completionUserInfoByAccount(
    account: String,
    userInfo: UserInfoEntity
  ): UserInfoEntity?

  fun updatePasswordByAccountAndOldPassword(
    account: String,
    oldPwd: String,
    newPwd: String
  ): UserEntity?

  fun verifyPassword(account: String?, pwd: String?): Boolean

  fun findUserAuthorizationModelByAccount(account: String): UserAuthorizationModel?
  fun findUserById(id: Long): UserEntity?
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

  fun registerUserGroup(req: PostUserGroupRequestParam): UserGroupEntity?
  fun findAllUserGroupByUser(user: UserEntity): Set<UserGroupEntity>
  fun deleteUserByAccount(account: String)
  fun assignUserToUserGroupById(userId: Long, userGroupId: Long)
}

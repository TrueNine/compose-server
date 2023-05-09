package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.relationship.RoleGroupRoleEntity
import com.truenine.component.rds.entity.relationship.RolePermissionsEntity
import com.truenine.component.rds.entity.relationship.UserGroupRoleGroupEntity
import com.truenine.component.rds.entity.relationship.UserRoleGroupEntity

/**
 * # 角色权限管理器
 */
interface RbacAggregator {
  fun findAllRoleNameByUserAccount(account: String): Set<String>
  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: String): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: String, userId: String): UserRoleGroupEntity?
  fun saveAllRoleGroupToUser(roleGroupIds: List<String>, userId: String): List<UserRoleGroupEntity>
  fun revokeRoleGroupFromUser(roleGroupId: String, userId: String)
  fun revokeAllRoleGroupFromUser(roleGroupIds: List<String>, userId: String)

  fun saveRoleGroupToUserGroup(roleGroupId: String, userGroupId: String): UserGroupRoleGroupEntity?
  fun saveAllRoleGroupToUserGroup(roleGroupIds: List<String>, userGroupId: String): List<UserGroupRoleGroupEntity>
  fun revokeRoleGroupFromUserGroup(roleGroupId: String, userGroupId: String)
  fun revokeAllRoleGroupFromUserGroup(roleGroupIds: List<String>, userGroupId: String)

  fun saveRoleToRoleGroup(roleId: String, roleGroupId: String): RoleGroupRoleEntity?
  fun saveAllRoleToRoleGroup(roleIds: List<String>, roleGroupId: String): List<RoleGroupRoleEntity>
  fun revokeRoleFromRoleGroup(roleId: String, roleGroupId: String)
  fun revokeAllRoleFromRoleGroup(roleIds: List<String>, roleGroupId: String)

  fun savePermissionsToRole(permissionsId: String, roleId: String): RolePermissionsEntity?
  fun saveAllPermissionsToRole(permissionsIds: List<String>, roleId: String): List<RolePermissionsEntity>
  fun revokePermissionsFromRole(permissionsId: String, roleId: String)
  fun revokeAllPermissionsFromRole(permissionsIds: List<String>, roleId: String)
}

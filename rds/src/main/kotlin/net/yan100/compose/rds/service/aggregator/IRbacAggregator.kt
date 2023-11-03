package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.entity.relationship.RoleGroupRole
import net.yan100.compose.rds.entity.relationship.RolePermissions
import net.yan100.compose.rds.entity.relationship.UserRoleGroup

/**
 * # 角色权限管理器
 */
interface IRbacAggregator {
  fun findAllRoleNameByUserAccount(account: String): Set<String>
  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: String): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: String, userId: String): UserRoleGroup?
  fun saveAllRoleGroupToUser(roleGroupIds: List<String>, userId: String): List<UserRoleGroup>
  fun revokeRoleGroupFromUser(roleGroupId: String, userId: String)
  fun revokeAllRoleGroupFromUser(roleGroupIds: List<String>, userId: String)

  fun saveRoleToRoleGroup(roleId: String, roleGroupId: String): RoleGroupRole?
  fun saveAllRoleToRoleGroup(roleIds: List<String>, roleGroupId: String): List<RoleGroupRole>
  fun revokeRoleFromRoleGroup(roleId: String, roleGroupId: String)
  fun revokeAllRoleFromRoleGroup(roleIds: List<String>, roleGroupId: String)

  fun savePermissionsToRole(permissionsId: String, roleId: String): RolePermissions?
  fun saveAllPermissionsToRole(permissionsIds: List<String>, roleId: String): List<RolePermissions>
  fun revokePermissionsFromRole(permissionsId: String, roleId: String)
  fun revokeAllPermissionsFromRole(permissionsIds: List<String>, roleId: String)
}

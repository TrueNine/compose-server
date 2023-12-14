package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.rds.entities.relationship.RoleGroupRole
import net.yan100.compose.rds.entities.relationship.RolePermissions
import net.yan100.compose.rds.entities.relationship.UserRoleGroup

/**
 * # 角色权限管理器
 */
interface IRbacAggregator {
  fun findAllRoleNameByUserAccount(account: String): Set<String>
  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: ReferenceId): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: ReferenceId, userId: ReferenceId): UserRoleGroup?
  fun saveAllRoleGroupToUser(roleGroupIds: List<ReferenceId>, userId: ReferenceId): List<UserRoleGroup>
  fun revokeRoleGroupFromUser(roleGroupId: ReferenceId, userId: ReferenceId)
  fun revokeAllRoleGroupFromUser(roleGroupIds: List<ReferenceId>, userId: ReferenceId)

  fun saveRoleToRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId): RoleGroupRole?
  fun saveAllRoleToRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId): List<RoleGroupRole>
  fun revokeRoleFromRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId)
  fun revokeAllRoleFromRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId)

  fun savePermissionsToRole(permissionsId: ReferenceId, roleId: ReferenceId): RolePermissions?
  fun saveAllPermissionsToRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId): List<RolePermissions>
  fun revokePermissionsFromRole(permissionsId: ReferenceId, roleId: ReferenceId)
  fun revokeAllPermissionsFromRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId)
}

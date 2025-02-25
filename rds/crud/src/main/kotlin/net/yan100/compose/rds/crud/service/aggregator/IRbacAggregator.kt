package net.yan100.compose.rds.crud.service.aggregator

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.crud.entities.jpa.RoleGroupRole
import net.yan100.compose.rds.crud.entities.jpa.RolePermissions
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup

/** # 角色权限管理器 */
interface IRbacAggregator {
  fun fetchAllRoleNameByUserAccount(account: String): Set<String>

  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: RefId): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: RefId, userId: RefId): UserRoleGroup?

  fun saveAllRoleGroupToUser(
    roleGroupIds: List<RefId>,
    userId: RefId,
  ): List<UserRoleGroup>

  fun revokeRoleGroupFromUser(roleGroupId: RefId, userId: RefId)

  fun revokeAllRoleGroupFromUser(roleGroupIds: List<RefId>, userId: RefId)

  fun linkRoleToRoleGroup(roleId: RefId, roleGroupId: RefId): RoleGroupRole?

  fun linkAllRoleToRoleGroup(
    roleIds: List<RefId>,
    roleGroupId: RefId,
  ): List<RoleGroupRole>

  fun revokeRoleFromRoleGroup(roleId: RefId, roleGroupId: RefId)

  fun revokeAllRoleFromRoleGroup(roleIds: List<RefId>, roleGroupId: RefId)

  fun savePermissionsToRole(
    permissionsId: RefId,
    roleId: RefId,
  ): RolePermissions?

  fun saveAllPermissionsToRole(
    permissionsIds: List<RefId>,
    roleId: RefId,
  ): List<RolePermissions>

  fun revokePermissionsFromRole(permissionsId: RefId, roleId: RefId)

  fun revokeAllPermissionsFromRole(permissionsIds: List<RefId>, roleId: RefId)
}

package net.yan100.compose.rds.service.aggregator

/**
 * # 角色权限管理器
 */
interface RbacAggregator {
  fun findAllRoleNameByUserAccount(account: String): Set<String>
  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: String): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: String, userId: String): net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity?
  fun saveAllRoleGroupToUser(roleGroupIds: List<String>, userId: String): List<net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity>
  fun revokeRoleGroupFromUser(roleGroupId: String, userId: String)
  fun revokeAllRoleGroupFromUser(roleGroupIds: List<String>, userId: String)

  fun saveRoleGroupToUserGroup(roleGroupId: String, userGroupId: String): net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity?
  fun saveAllRoleGroupToUserGroup(
    roleGroupIds: List<String>,
    userGroupId: String
  ): List<net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity>

  fun revokeRoleGroupFromUserGroup(roleGroupId: String, userGroupId: String)
  fun revokeAllRoleGroupFromUserGroup(roleGroupIds: List<String>, userGroupId: String)

  fun saveRoleToRoleGroup(roleId: String, roleGroupId: String): net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity?
  fun saveAllRoleToRoleGroup(roleIds: List<String>, roleGroupId: String): List<net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity>
  fun revokeRoleFromRoleGroup(roleId: String, roleGroupId: String)
  fun revokeAllRoleFromRoleGroup(roleIds: List<String>, roleGroupId: String)

  fun savePermissionsToRole(permissionsId: String, roleId: String): net.yan100.compose.rds.entity.relationship.RolePermissionsEntity?
  fun saveAllPermissionsToRole(permissionsIds: List<String>, roleId: String): List<net.yan100.compose.rds.entity.relationship.RolePermissionsEntity>
  fun revokePermissionsFromRole(permissionsId: String, roleId: String)
  fun revokeAllPermissionsFromRole(permissionsIds: List<String>, roleId: String)
}

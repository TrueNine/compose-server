package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.RoleGroupRoleEntity
import com.truenine.component.rds.entity.RolePermissionsEntity
import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import com.truenine.component.rds.entity.UserRoleGroupEntity

/**
 * 角色权限管理器
 */
interface RbacAggregator {
  fun saveRoleGroupToUser(roleGroupId: Long, userId: Long): UserRoleGroupEntity?
  fun saveAllRoleGroupToUser(roleGroupIds: List<Long>, userId: Long): List<UserRoleGroupEntity>
  fun revokeRoleGroupFromUser(roleGroupId: Long, userId: Long)
  fun revokeAllRoleGroupFromUser(roleGroupIds: List<Long>, userId: Long)

  fun saveRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long): UserGroupRoleGroupEntity?
  fun saveAllRoleGroupToUserGroup(roleGroupIds: List<Long>, userGroupId: Long): List<UserGroupRoleGroupEntity>
  fun revokeRoleGroupFromUserGroup(roleGroupId: Long, userGroupId: Long)
  fun revokeAllRoleGroupFromUserGroup(roleGroupIds: List<Long>, userGroupId: Long)

  fun saveRoleToRoleGroup(roleId: Long, roleGroupId: Long): RoleGroupRoleEntity?
  fun saveAllRoleToRoleGroup(roleIds: List<Long>, roleGroupId: Long): List<RoleGroupRoleEntity>
  fun revokeRoleFromRoleGroup(roleId: Long, roleGroupId: Long)
  fun revokeAllRoleFromRoleGroup(roleIds: List<Long>, roleGroupId: Long)

  fun savePermissionsToRole(permissionsId: Long, roleId: Long): RolePermissionsEntity?
  fun saveAllPermissionsToRole(permissionsIds: List<Long>, roleId: Long): List<RolePermissionsEntity>
  fun revokePermissionsFromRole(permissionsId: Long, roleId: Long)
  fun revokeAllPermissionsFromRole(permissionsIds: List<Long>, roleId: Long)
}

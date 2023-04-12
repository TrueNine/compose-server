package com.truenine.component.rds.service

import com.truenine.component.rds.entity.*


interface RbacService {
  fun findAllRoleGroupByUserGroup(userGroup: UserGroupEntity): Set<RoleGroupEntity>
  fun findAllRoleByRoleGroup(roleGroup: RoleGroupEntity): Set<RoleEntity>
  fun findRoleById(id: String): RoleEntity?
  fun findAllRoleGroupByName(name: String): Set<RoleGroupEntity>
  fun findAllRoleByName(name: String): Set<RoleEntity>
  fun findAllPermissionsByName(name: String): Set<PermissionsEntity>

  fun findPlainRoleGroup(): RoleGroupEntity
  fun findRootRoleGroup(): RoleGroupEntity

  fun findAllRoleGroupByUser(user: UserEntity): Set<RoleGroupEntity>

  fun findAllRoleByUser(user: UserEntity): Set<RoleEntity>
  fun findAllPermissionsByUser(user: UserEntity): Set<PermissionsEntity>

  fun findAllPermissionsByRole(role: RoleEntity): Set<PermissionsEntity>

  fun assignRoleGroupToUser(roleGroup: RoleGroupEntity, user: UserEntity):UserRoleGroupEntity?

  fun revokeRoleGroupByUser(
    roleGroup: RoleGroupEntity,
    user: UserEntity
  )

  fun revokeAllRoleGroupByUser(user: UserEntity)
  fun revokeAllRoleGroupByUserGroup(userGroup: UserGroupEntity)
  fun assignRoleGroupToUserGroup(roleGroup: RoleGroupEntity, userGroup: UserGroupEntity): UserGroupRoleGroupEntity?

  fun revokeRoleGroupForUserGroup(
    roleGroup: RoleGroupEntity,
    userGroup: UserGroupEntity
  )

  fun saveRoleGroup(roleGroup: RoleGroupEntity): RoleGroupEntity
  fun assignRoleToRoleGroup(roleGroup: RoleGroupEntity, role: RoleEntity): RoleGroupRoleEntity

  fun revokeRoleForRoleGroup(
    roleGroup: RoleGroupEntity,
    role: RoleEntity
  )

  fun saveRole(role: RoleEntity): RoleEntity
  fun assignPermissionsToRole(
    role: RoleEntity,
    permissions: PermissionsEntity
  ): RolePermissionsEntity?

  fun revokePermissionsForRole(
    role: RoleEntity,
    permissions: PermissionsEntity
  )

  fun savePermissions(permissions: PermissionsEntity): PermissionsEntity

  fun deleteRoleGroup(roleGroup: RoleGroupEntity)
  fun deleteRole(role: RoleEntity)
  fun deletePermissions(permissions: PermissionsEntity)
  fun findRoleGroupById(id: String): RoleGroupEntity?
  fun findPermissionsById(id: String): PermissionsEntity?
}

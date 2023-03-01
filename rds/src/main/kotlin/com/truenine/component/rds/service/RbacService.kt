package com.truenine.component.rds.service

import com.truenine.component.rds.dao.*


interface RbacService {
  fun findAllRoleGroupByUserGroup(userGroup: UserGroupDao): Set<RoleGroupDao>
  fun findAllRoleByRoleGroup(roleGroup: RoleGroupDao): Set<RoleDao>
  fun findRoleById(id: String): RoleDao?
  fun findAllRoleGroupByName(name: String): Set<RoleGroupDao>
  fun findAllRoleByName(name: String): Set<RoleDao>
  fun findAllPermissionsByName(name: String): Set<PermissionsDao>

  fun findPlainRoleGroup(): RoleGroupDao
  fun findRootRoleGroup(): RoleGroupDao

  fun findAllRoleGroupByUser(user: UserDao): Set<RoleGroupDao>

  fun findAllRoleByUser(user: UserDao): Set<RoleDao>
  fun findAllPermissionsByUser(user: UserDao): Set<PermissionsDao>

  fun findAllPermissionsByRole(role: RoleDao): Set<PermissionsDao>

  fun assignRoleGroupToUser(
    roleGroup: RoleGroupDao,
    user: UserDao
  )

  fun revokeRoleGroupByUser(
    roleGroup: RoleGroupDao,
    user: UserDao
  )

  fun revokeAllRoleGroupByUser(user: UserDao)
  fun revokeAllRoleGroupByUserGroup(userGroup: UserGroupDao)
  fun assignRoleGroupToUserGroup(
    roleGroup: RoleGroupDao,
    userGroup: UserGroupDao
  )

  fun revokeRoleGroupForUserGroup(
    roleGroup: RoleGroupDao,
    userGroup: UserGroupDao
  )

  fun saveRoleGroup(roleGroup: RoleGroupDao): RoleGroupDao
  fun assignRoleToRoleGroup(
    roleGroup: RoleGroupDao,
    role: RoleDao
  )

  fun revokeRoleForRoleGroup(
    roleGroup: RoleGroupDao,
    role: RoleDao
  )

  fun saveRole(role: RoleDao): RoleDao
  fun assignPermissionsToRole(
    role: RoleDao,
    permissions: PermissionsDao
  )

  fun revokePermissionsForRole(
    role: RoleDao,
    permissions: PermissionsDao
  )

  fun savePermissions(permissions: PermissionsDao): PermissionsDao

  fun deleteRoleGroup(roleGroup: RoleGroupDao)
  fun deleteRole(role: RoleDao)
  fun deletePermissions(permissions: PermissionsDao)
  fun findRoleGroupById(id: String): RoleGroupDao?
  fun findPermissionsById(id: String): PermissionsDao?
}

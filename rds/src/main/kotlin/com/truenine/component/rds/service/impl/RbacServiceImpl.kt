package com.truenine.component.rds.service.impl

import com.truenine.component.core.consts.Bf
import com.truenine.component.rds.dao.*
import com.truenine.component.rds.repo.*
import com.truenine.component.rds.service.RbacService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class RbacServiceImpl(
  private val roleGroupRoleRepo: RoleGroupRoleRepo,
  private val roleRepo: RoleRepo,
  private val roleGroupRepo: RoleGroupRepo,
  private val permissionsRepo: PermissionsRepo,
  private val rolePermissionsRepo: RolePermissionsRepo,
  private val userRoleGroupRepo: UserRoleGroupRepo,
  private val userGroupRoleGroupRepo: UserGroupRoleGroupRepo
) : RbacService {
  override fun findAllRoleGroupByUserGroup(userGroup: UserGroupDao): Set<RoleGroupDao> {
    return userGroupRoleGroupRepo
      .findAllByUserGroupId(userGroup.id)
      .map { it.roleGroupId }
      .run { roleGroupRepo.findAllById(this).toSet() }
  }

  override fun findAllRoleByRoleGroup(roleGroup: RoleGroupDao): Set<RoleDao> {
    return roleGroupRoleRepo
      .findAllByRoleGroupId(roleGroup.id)
      .map { it.roleId }
      .run { roleRepo.findAllById(this).toSet() }
  }

  override fun findRoleById(id: String): RoleDao? {
    return roleRepo.findById(id).orElse(null)
  }

  override fun findAllRoleGroupByName(name: String): Set<RoleGroupDao> {
    return roleGroupRepo.findAllByName(name).toSet()
  }


  override fun findPlainRoleGroup(): RoleGroupDao {
    return roleGroupRepo.findById(Bf.Rbac.USER_ID).orElse(null)
  }

  override fun findRootRoleGroup(): RoleGroupDao {
    return roleGroupRepo.findById(Bf.Rbac.ROOT_ID).orElse(null)
  }

  override fun findAllRoleByName(name: String): Set<RoleDao> {
    return roleRepo.findAllByName(name).toSet()
  }

  override fun findAllPermissionsByName(name: String): Set<PermissionsDao> {
    return permissionsRepo.findAllByName(name).toSet()
  }

  override fun findAllRoleGroupByUser(user: UserDao): Set<RoleGroupDao> {
    return roleGroupRepo.findAllByUserId(user.id).toSet()
  }

  override fun findAllRoleByUser(user: UserDao): Set<RoleDao> {
    return roleRepo.findAllByUserId(user.id).toSet()
  }

  override fun findAllPermissionsByUser(user: UserDao): Set<PermissionsDao> {
    return permissionsRepo.findAllByUserId(user.id).toSet()
  }

  override fun findAllPermissionsByRole(role: RoleDao): Set<PermissionsDao> {
    return rolePermissionsRepo.findAllByRoleId(role.id)
      .map { it.permissionsId }
      .run { permissionsRepo.findAllById(this).toSet() }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignRoleGroupToUser(
    roleGroup: RoleGroupDao,
    user: UserDao
  ) {
    UserRoleGroupDao().apply {
      userId = user.id
      roleGroupId = roleGroup.id
    }.run {
      if (!userRoleGroupRepo.existsByUserIdAndRoleGroupId(
          this.userId,
          this.roleGroupId
        )
      ) {
        userRoleGroupRepo.save(this)
      }
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupByUser(
    roleGroup: RoleGroupDao,
    user: UserDao
  ) {
    UserRoleGroupDao().apply {
      this.roleGroupId = roleGroup.id
      this.userId = user.id
    }.run {
      userRoleGroupRepo.deleteByUserIdAndRoleGroupId(
        userId,
        roleGroupId
      )
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupByUser(user: UserDao) {
    userRoleGroupRepo.deleteAllByUserId(user.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupByUserGroup(userGroup: UserGroupDao) {
    userGroupRoleGroupRepo.deleteAllByUserGroupId(userGroupId = userGroup.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignRoleGroupToUserGroup(
    roleGroup: RoleGroupDao,
    userGroup: UserGroupDao
  ) {
    UserGroupRoleGroupDao()
      .apply {
        this.roleGroupId = roleGroup.id
        this.userGroupId = userGroup.id
      }.run {
        if (!userGroupRoleGroupRepo.existsByUserGroupIdAndRoleGroupId(
            userGroupId,
            roleGroupId
          )
        ) {
          userGroupRoleGroupRepo.save(this)
        }
      }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupForUserGroup(
    roleGroup: RoleGroupDao,
    userGroup: UserGroupDao
  ) {
    UserGroupRoleGroupDao()
      .apply {
        this.userGroupId = userGroup.id
        this.roleGroupId = roleGroup.id
      }.run {
        userGroupRoleGroupRepo.deleteByUserGroupIdAndRoleGroupId(
          userGroupId,
          roleGroupId
        )
      }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun saveRoleGroup(roleGroup: RoleGroupDao): RoleGroupDao {
    return roleGroupRepo.save(roleGroup)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignRoleToRoleGroup(
    roleGroup: RoleGroupDao,
    role: RoleDao
  ) {
    RoleGroupRoleDao().apply {
      this.roleGroupId = roleGroup.id
      this.roleId = role.id
    }.run {
      if (!roleGroupRoleRepo.existsByRoleGroupIdAndRoleId(
          roleGroupId,
          roleId
        )
      ) {
        roleGroupRoleRepo.save(this)
      }
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleForRoleGroup(
    roleGroup: RoleGroupDao,
    role: RoleDao
  ) {
    RoleGroupRoleDao().apply {
      this.roleGroupId = roleGroup.id
      this.roleId = role.id
    }.run {
      roleGroupRoleRepo.deleteByRoleGroupIdAndRoleId(
        roleGroupId,
        roleId
      )
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun saveRole(role: RoleDao): RoleDao {
    return roleRepo.save(role)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignPermissionsToRole(
    role: RoleDao,
    permissions: PermissionsDao
  ) {
    RolePermissionsDao()
      .apply {
        this.permissionsId = permissions.id
        this.roleId = role.id
      }.run {
        if (!rolePermissionsRepo.existsByRoleIdAndPermissionsId(
            roleId,
            permissionsId
          )
        ) {
          rolePermissionsRepo.save(this)
        }
      }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokePermissionsForRole(
    role: RoleDao,
    permissions: PermissionsDao
  ) {
    rolePermissionsRepo.deleteByRoleIdAndPermissionsId(role.id, permissions.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun savePermissions(permissions: PermissionsDao): PermissionsDao {
    return permissionsRepo.save(permissions)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteRoleGroup(roleGroup: RoleGroupDao) {
    roleGroupRepo.deleteById(roleGroup.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteRole(role: RoleDao) {
    roleRepo.deleteById(role.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deletePermissions(permissions: PermissionsDao) {
    permissionsRepo.deleteById(permissions.id)
  }

  override fun findRoleGroupById(id: String): RoleGroupDao? {
    return roleGroupRepo.findById(id).orElse(null)
  }

  override fun findPermissionsById(id: String): PermissionsDao? {
    return permissionsRepo.findById(id).orElse(null)
  }
}

package com.truenine.component.rds.service.impl

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.rds.entity.*
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
  override fun findAllRoleGroupByUserGroup(userGroup: UserGroupEntity): Set<RoleGroupEntity> {
    return userGroupRoleGroupRepo
      .findAllByUserGroupId(userGroup.id)
      .map { it.roleGroupId }
      .run { roleGroupRepo.findAllById(this).toSet() }
  }

  override fun findAllRoleByRoleGroup(roleGroup: RoleGroupEntity): Set<RoleEntity> {
    return roleGroupRoleRepo
      .findAllByRoleGroupId(roleGroup.id)
      .map { it.roleId }
      .run { roleRepo.findAllById(this).toSet() }
  }

  override fun findRoleById(id: String): RoleEntity? {
    return roleRepo.findById(id).orElse(null)
  }

  override fun findAllRoleGroupByName(name: String): Set<RoleGroupEntity> {
    return roleGroupRepo.findAllByName(name).toSet()
  }


  override fun findPlainRoleGroup(): RoleGroupEntity {
    return roleGroupRepo.findById(DataBaseBasicFieldNames.Rbac.USER_ID).orElse(null)
  }

  override fun findRootRoleGroup(): RoleGroupEntity {
    return roleGroupRepo.findById(DataBaseBasicFieldNames.Rbac.ROOT_ID).orElse(null)
  }

  override fun findAllRoleByName(name: String): Set<RoleEntity> {
    return roleRepo.findAllByName(name).toSet()
  }

  override fun findAllPermissionsByName(name: String): Set<PermissionsEntity> {
    return permissionsRepo.findAllByName(name).toSet()
  }

  override fun findAllRoleGroupByUser(user: UserEntity): Set<RoleGroupEntity> {
    return roleGroupRepo.findAllByUserId(user.id).toSet()
  }

  override fun findAllRoleByUser(user: UserEntity): Set<RoleEntity> {
    return roleRepo.findAllByUserId(user.id).toSet()
  }

  override fun findAllPermissionsByUser(user: UserEntity): Set<PermissionsEntity> {
    return permissionsRepo.findAllByUserId(user.id).toSet()
  }

  override fun findAllPermissionsByRole(role: RoleEntity): Set<PermissionsEntity> {
    return rolePermissionsRepo.findAllByRoleId(role.id)
      .map { it.permissionsId }
      .run { permissionsRepo.findAllById(this).toSet() }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignRoleGroupToUser(
    roleGroup: RoleGroupEntity,
    user: UserEntity
  ) {
    UserRoleGroupEntity().apply {
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
    roleGroup: RoleGroupEntity,
    user: UserEntity
  ) {
    UserRoleGroupEntity().apply {
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
  override fun revokeAllRoleGroupByUser(user: UserEntity) {
    userRoleGroupRepo.deleteAllByUserId(user.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupByUserGroup(userGroup: UserGroupEntity) {
    userGroupRoleGroupRepo.deleteAllByUserGroupId(userGroupId = userGroup.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignRoleGroupToUserGroup(
    roleGroup: RoleGroupEntity,
    userGroup: UserGroupEntity
  ) {
    UserGroupRoleGroupEntity()
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
    roleGroup: RoleGroupEntity,
    userGroup: UserGroupEntity
  ) {
    UserGroupRoleGroupEntity()
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
  override fun saveRoleGroup(roleGroup: RoleGroupEntity): RoleGroupEntity {
    return roleGroupRepo.save(roleGroup)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignRoleToRoleGroup(
    roleGroup: RoleGroupEntity,
    role: RoleEntity
  ) {
    RoleGroupRoleEntity().apply {
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
    roleGroup: RoleGroupEntity,
    role: RoleEntity
  ) {
    RoleGroupRoleEntity().apply {
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
  override fun saveRole(role: RoleEntity): RoleEntity {
    return roleRepo.save(role)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignPermissionsToRole(
    role: RoleEntity,
    permissions: PermissionsEntity
  ) {
    RolePermissionsEntity()
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
    role: RoleEntity,
    permissions: PermissionsEntity
  ) {
    rolePermissionsRepo.deleteByRoleIdAndPermissionsId(role.id, permissions.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun savePermissions(permissions: PermissionsEntity): PermissionsEntity {
    return permissionsRepo.save(permissions)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteRoleGroup(roleGroup: RoleGroupEntity) {
    roleGroupRepo.deleteById(roleGroup.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteRole(role: RoleEntity) {
    roleRepo.deleteById(role.id)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deletePermissions(permissions: PermissionsEntity) {
    permissionsRepo.deleteById(permissions.id)
  }

  override fun findRoleGroupById(id: String): RoleGroupEntity? {
    return roleGroupRepo.findById(id).orElse(null)
  }

  override fun findPermissionsById(id: String): PermissionsEntity? {
    return permissionsRepo.findById(id).orElse(null)
  }
}

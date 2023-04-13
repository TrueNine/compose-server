package com.truenine.component.rds.service.aggregator

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.rds.entity.*
import com.truenine.component.rds.repo.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RbacServiceImpl(
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
      .let { roleGroupRepo.findAllById(it).toSet() }
  }

  override fun findAllRoleByRoleGroup(roleGroup: RoleGroupEntity): Set<RoleEntity> {
    return roleGroupRoleRepo
      .findAllByRoleGroupId(roleGroup.id)
      .map { it.roleId }
      .let { roleRepo.findAllById(it).toSet() }
  }

  override fun findRoleById(id: Long): RoleEntity? {
    return roleRepo.findById(id).orElse(null)
  }

  override fun findAllRoleGroupByName(name: String): Set<RoleGroupEntity> {
    return roleGroupRepo.findAllByName(name).toSet()
  }


  override fun findPlainRoleGroup(): RoleGroupEntity? {
    return roleGroupRepo.findByIdOrNull(DataBaseBasicFieldNames.Rbac.USER_ID.toLong())
  }

  override fun findRootRoleGroup(): RoleGroupEntity? {
    return roleGroupRepo.findByIdOrNull(DataBaseBasicFieldNames.Rbac.ROOT_ID)
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
      .let { permissionsRepo.findAllById(it).toSet() }
  }


  override fun assignRoleGroupToUser(roleGroup: RoleGroupEntity, user: UserEntity): UserRoleGroupEntity? {
    val saved = userRoleGroupRepo.findByUserIdAndRoleGroupId(user.id, roleGroup.id)
    return if (null != saved) {
      saved
    } else {
      val a = UserRoleGroupEntity().apply {
        userId = user.id
        roleGroupId = roleGroup.id
      }
      userRoleGroupRepo.save(a)
    }
  }

  override fun revokeRoleGroupByUser(roleGroup: RoleGroupEntity, user: UserEntity) = userRoleGroupRepo.deleteByUserIdAndRoleGroupId(user.id, roleGroup.id)

  override fun revokeAllRoleGroupByUser(user: UserEntity) = userRoleGroupRepo.deleteAllByUserId(user.id)


  override fun revokeAllRoleGroupByUserGroup(userGroup: UserGroupEntity) = userGroupRoleGroupRepo.deleteAllByUserGroupId(userGroupId = userGroup.id)


  override fun assignRoleGroupToUserGroup(
    roleGroup: RoleGroupEntity,
    userGroup: UserGroupEntity
  ): UserGroupRoleGroupEntity? {
    val saved = userGroupRoleGroupRepo.findByUserGroupIdAndRoleGroupId(userGroup.id, roleGroup.id)
    return if (saved != null) {
      saved
    } else {
      val ugrg = UserGroupRoleGroupEntity().apply {
        roleGroupId = roleGroup.id
        userGroupId = userGroup.id
      }
      userGroupRoleGroupRepo.save(ugrg)
    }
  }

  override fun revokeRoleGroupForUserGroup(roleGroup: RoleGroupEntity, userGroup: UserGroupEntity) =
    userGroupRoleGroupRepo.deleteByUserGroupIdAndRoleGroupId(userGroup.id, roleGroup.id)

  override fun saveRoleGroup(roleGroup: RoleGroupEntity) = roleGroupRepo.save(roleGroup)

  override fun assignRoleToRoleGroup(
    roleGroup: RoleGroupEntity,
    role: RoleEntity
  ): RoleGroupRoleEntity {
    val saved = roleGroupRoleRepo.findByRoleGroupIdAndRoleId(roleGroup.id, role.id)
    if (saved != null) {
      return saved
    }
    val rg = RoleGroupRoleEntity().apply {
      roleId = role.id
      roleGroupId = roleGroup.id
    }
    return roleGroupRoleRepo.save(rg)
  }

  override fun revokeRoleForRoleGroup(
    roleGroup: RoleGroupEntity,
    role: RoleEntity
  ) = roleGroupRoleRepo.deleteByRoleGroupIdAndRoleId(roleGroup.id, role.id)

  override fun saveRole(role: RoleEntity): RoleEntity = roleRepo.save(role)

  override fun assignPermissionsToRole(
    role: RoleEntity,
    permissions: PermissionsEntity
  ): RolePermissionsEntity? {
    val saved = rolePermissionsRepo.findByRoleIdAndPermissionsId(role.id, permissions.id)
    if (saved != null) {
      return saved
    }
    val rolePermissions = RolePermissionsEntity().apply {
      permissionsId = permissions.id
      roleId = role.id
    }
    return rolePermissionsRepo.save(rolePermissions)
  }


  override fun revokePermissionsForRole(
    role: RoleEntity,
    permissions: PermissionsEntity
  ) = rolePermissionsRepo.deleteByRoleIdAndPermissionsId(role.id, permissions.id)

  override fun savePermissions(permissions: PermissionsEntity): PermissionsEntity = permissionsRepo.save(permissions)

  override fun deleteRoleGroup(roleGroup: RoleGroupEntity) = roleGroupRepo.deleteById(roleGroup.id)

  override fun deleteRole(role: RoleEntity) = roleRepo.deleteById(role.id)

  override fun deletePermissions(permissions: PermissionsEntity) = permissionsRepo.deleteById(permissions.id)

  override fun findRoleGroupById(id: Long): RoleGroupEntity? = roleGroupRepo.findByIdOrNull(id)

  override fun findPermissionsById(id: Long): PermissionsEntity? = permissionsRepo.findByIdOrNull(id)
}

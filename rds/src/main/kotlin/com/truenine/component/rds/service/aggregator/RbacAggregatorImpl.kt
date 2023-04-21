package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.*
import com.truenine.component.rds.repository.RoleGroupRoleRepository
import com.truenine.component.rds.repository.RolePermissionsRepository
import com.truenine.component.rds.repository.UserGroupRoleGroupRepository
import com.truenine.component.rds.repository.UserRoleGroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RbacAggregatorImpl(
  private val urg: UserRoleGroupRepository,
  private val ugrg: UserGroupRoleGroupRepository,
  private val rgr: RoleGroupRoleRepository,
  private val rp: RolePermissionsRepository
) : RbacAggregator {
  override fun saveRoleGroupToUser(roleGroupId: Long, userId: Long): UserRoleGroupEntity? =
    urg.findByUserIdAndRoleGroupId(userId, roleGroupId)
      ?: urg.save(UserRoleGroupEntity().apply {
        this.userId = userId
        this.roleGroupId = roleGroupId
      })

  override fun saveAllRoleGroupToUser(roleGroupIds: List<Long>, userId: Long): List<UserRoleGroupEntity> {
    val existingRoleGroups = urg.findAllRoleGroupIdByUserId(userId)
    val mewRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      UserRoleGroupEntity().apply {
        roleGroupId = it
        this.userId = userId
      }
    }
    return urg.saveAll(mewRoleGroups)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUser(roleGroupId: Long, userId: Long) = urg.deleteAllByRoleGroupIdAndUserId(roleGroupId, userId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUser(roleGroupIds: List<Long>, userId: Long) =
    urg.deleteAllByRoleGroupIdInAndUserId(roleGroupIds, userId)

  override fun saveRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long): UserGroupRoleGroupEntity? =
    ugrg.findByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)
      ?: ugrg.save(UserGroupRoleGroupEntity().apply {
        this.roleGroupId = roleGroupId
        this.userGroupId = userGroupId
      })

  override fun saveAllRoleGroupToUserGroup(roleGroupIds: List<Long>, userGroupId: Long): List<UserGroupRoleGroupEntity> {
    val existingRoleGroups = ugrg.findAllRoleGroupIdByUserGroupId(userGroupId)
    val newRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      UserGroupRoleGroupEntity().apply {
        this.userGroupId = userGroupId
        roleGroupId = it
      }
    }
    return ugrg.saveAll(newRoleGroups)
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUserGroup(roleGroupId: Long, userGroupId: Long) =
    ugrg.deleteByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUserGroup(roleGroupIds: List<Long>, userGroupId: Long) =
    ugrg.deleteAllByRoleGroupIdInAndUserGroupId(roleGroupIds, userGroupId)

  override fun saveRoleToRoleGroup(roleId: Long, roleGroupId: Long): RoleGroupRoleEntity? =
    rgr.findByRoleGroupIdAndRoleId(roleGroupId, roleId)
      ?: rgr.save(RoleGroupRoleEntity().apply {
        this.roleGroupId = roleGroupId
        this.roleId = roleId
      })

  override fun saveAllRoleToRoleGroup(roleIds: List<Long>, roleGroupId: Long): List<RoleGroupRoleEntity> {
    val existingRoles = rgr.findAllRoleIdByRoleGroupId(roleGroupId)
    val newRoles = roleIds.filterNot { existingRoles.contains(it) }.map {
      RoleGroupRoleEntity().apply {
        this.roleGroupId = roleGroupId
        roleId = it
      }
    }
    return rgr.saveAll(newRoles)
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleFromRoleGroup(roleId: Long, roleGroupId: Long) =
    rgr.deleteByRoleGroupIdAndRoleId(roleGroupId, roleId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleFromRoleGroup(roleIds: List<Long>, roleGroupId: Long) = rgr.deleteAllByRoleIdInAndRoleGroupId(roleIds, roleGroupId)

  override fun savePermissionsToRole(permissionsId: Long, roleId: Long): RolePermissionsEntity? =
    rp.findByRoleIdAndPermissionsId(roleId, permissionsId)
      ?: rp.save(RolePermissionsEntity().apply {
        this.roleId = roleId
        this.permissionsId = permissionsId
      })

  override fun saveAllPermissionsToRole(permissionsIds: List<Long>, roleId: Long): List<RolePermissionsEntity> {
    val existingPermissions = rp.findAllPermissionsIdByRoleId(roleId)
    val newPermissions = permissionsIds.filterNot { existingPermissions.contains(it) }.map {
      RolePermissionsEntity().apply {
        permissionsId = it
        this.roleId = roleId
      }
    }
    return rp.saveAll(newPermissions)
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun revokePermissionsFromRole(permissionsId: Long, roleId: Long) =
    rp.deleteByRoleIdAndPermissionsId(roleId, permissionsId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllPermissionsFromRole(permissionsIds: List<Long>, roleId: Long) =
    rp.deleteAllByPermissionsIdInAndRoleId(permissionsIds, roleId)
}

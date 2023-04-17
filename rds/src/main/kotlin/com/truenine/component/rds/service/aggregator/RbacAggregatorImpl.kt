package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.RoleGroupRoleEntity
import com.truenine.component.rds.entity.RolePermissionsEntity
import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import com.truenine.component.rds.entity.UserRoleGroupEntity
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
    urg.save(UserRoleGroupEntity().apply {
      this.userId = userId
      this.roleGroupId = roleGroupId
    })

  override fun saveAllRoleGroupToUser(roleGroupIds: List<Long>, userId: Long): List<UserRoleGroupEntity> =
    urg.saveAll(roleGroupIds.map {
      UserRoleGroupEntity().apply {
        roleGroupId = it
        this.userId = userId
      }
    })

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUser(roleGroupId: Long, userId: Long) = urg.deleteAllByRoleGroupIdAndUserId(roleGroupId, userId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUser(roleGroupIds: List<Long>, userId: Long) =
    urg.deleteAllByRoleGroupIdInAndUserId(roleGroupIds, userId)

  override fun saveRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long): UserGroupRoleGroupEntity? =
    ugrg.save(UserGroupRoleGroupEntity().apply {
      this.roleGroupId = roleGroupId
      this.userGroupId = userGroupId
    })

  override fun saveAllRoleGroupToUserGroup(roleGroupIds: List<Long>, userGroupId: Long): List<UserGroupRoleGroupEntity> =
    ugrg.saveAll(roleGroupIds.map {
      UserGroupRoleGroupEntity().apply {
        this.roleGroupId = it
        this.userGroupId = userGroupId
      }
    })

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUserGroup(roleGroupId: Long, userGroupId: Long) =
    ugrg.deleteByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUserGroup(roleGroupIds: List<Long>, userGroupId: Long) =
    ugrg.deleteAllByRoleGroupIdInAndUserGroupId(roleGroupIds, userGroupId)

  override fun saveRoleToRoleGroup(roleId: Long, roleGroupId: Long): RoleGroupRoleEntity? =
    rgr.save(RoleGroupRoleEntity().apply {
      this.roleGroupId = roleGroupId
      this.roleId = roleId
    })

  override fun saveAllRoleToRoleGroup(roleIds: List<Long>, roleGroupId: Long): List<RoleGroupRoleEntity> =
    rgr.saveAll(roleIds.map {
      RoleGroupRoleEntity().apply {
        this.roleId = it
        this.roleGroupId = roleGroupId
      }
    })

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleFromRoleGroup(roleId: Long, roleGroupId: Long) =
    rgr.deleteByRoleGroupIdAndRoleId(roleGroupId, roleId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleFromRoleGroup(roleIds: List<Long>, roleGroupId: Long) = rgr.deleteAllByRoleIdInAndRoleGroupId(roleIds, roleGroupId)

  override fun savePermissionsToRole(permissionsId: Long, roleId: Long): RolePermissionsEntity? = rp.save(RolePermissionsEntity().apply {
    this.roleId = roleId
    this.permissionsId = permissionsId
  })

  override fun saveAllPermissionsToRole(permissionsIds: List<Long>, roleId: Long): List<RolePermissionsEntity> =
    rp.saveAll(permissionsIds.map {
      RolePermissionsEntity().apply {
        this.permissionsId = it
        this.roleId = roleId
      }
    })

  @Transactional(rollbackFor = [Exception::class])
  override fun revokePermissionsFromRole(permissionsId: Long, roleId: Long) =
    rp.deleteByRoleIdAndPermissionsId(roleId, permissionsId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllPermissionsFromRole(permissionsIds: List<Long>, roleId: Long) =
    rp.deleteAllByPermissionsIdInAndRoleId(permissionsIds, roleId)
}

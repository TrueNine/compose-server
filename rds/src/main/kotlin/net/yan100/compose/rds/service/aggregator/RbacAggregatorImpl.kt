package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.repository.AllRoleGroupEntityRepository
import net.yan100.compose.rds.repository.UserGroupRepository
import net.yan100.compose.rds.repository.UserRepository
import net.yan100.compose.rds.repository.relationship.RoleGroupRoleRepository
import net.yan100.compose.rds.repository.relationship.RolePermissionsRepository
import net.yan100.compose.rds.repository.relationship.UserGroupRoleGroupRepository
import net.yan100.compose.rds.repository.relationship.UserRoleGroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RbacAggregatorImpl(
  private val urg: UserRoleGroupRepository,
  private val ug: UserGroupRepository,
  private val userRepository: UserRepository,
  private val ugrg: UserGroupRoleGroupRepository,
  private val rgr: RoleGroupRoleRepository,
  private val rp: RolePermissionsRepository,
  private val rg: AllRoleGroupEntityRepository
) : RbacAggregator {

  override fun findAllRoleNameByUserAccount(account: String): Set<String> =
    userRepository.findAllRoleNameByAccount(account)

  override fun findAllPermissionsNameByUserAccount(account: String): Set<String> =
    userRepository.findAllPermissionsNameByAccount(account)


  override fun findAllSecurityNameByUserId(userId: String): Set<String> {
    // FIXME 待优化
    // 查询所有用户组的角色组id
    val leaderUserUserGroupRoleGroupIds = ug.findAllByUserId(userId).map { it.roleGroups }.flatten().map { it.id }
    val userGroupRoleGroupIds = ug.findAllByUserId(userId).map { it.roleGroups }.flatten().map { it.id }
    // 查询所有用户的角色组id
    val userRoleGroupIds = urg.findAllRoleGroupIdByUserId(userId)

    // 将之解包
    val allNames = with(
      leaderUserUserGroupRoleGroupIds
        + userGroupRoleGroupIds
        + userRoleGroupIds
    ) {
      val roleGroups = rg.findAllById(this)
      val roleNames = roleGroups.map { it.roles }.flatten().map { "ROLE_${it.name}" }
      val permissionNames = roleGroups
        .asSequence().map { it.roles }
        .flatten().map { it.permissions }
        .flatten().map { it.name }.toList()
      roleNames + permissionNames
    }
    return allNames.filterNotNull().toSet()
  }

  override fun findAllSecurityNameByAccount(account: String): Set<String> =
    findAllSecurityNameByUserId(userRepository.findIdByAccount(account))

  override fun saveRoleGroupToUser(roleGroupId: String, userId: String): net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity? =
    urg.findByUserIdAndRoleGroupId(userId, roleGroupId)
      ?: urg.save(net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity().apply {
        this.userId = userId
        this.roleGroupId = roleGroupId
      })

  override fun saveAllRoleGroupToUser(roleGroupIds: List<String>, userId: String): List<net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity> {
    val existingRoleGroups = urg.findAllRoleGroupIdByUserId(userId)
    val mewRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity().apply {
        roleGroupId = it
        this.userId = userId
      }
    }
    return urg.saveAll(mewRoleGroups)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUser(roleGroupId: String, userId: String) = urg.deleteAllByRoleGroupIdAndUserId(roleGroupId, userId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUser(roleGroupIds: List<String>, userId: String) =
    urg.deleteAllByRoleGroupIdInAndUserId(roleGroupIds, userId)

  override fun saveRoleGroupToUserGroup(roleGroupId: String, userGroupId: String): net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity? =
    ugrg.findByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)
      ?: ugrg.save(net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity().apply {
        this.roleGroupId = roleGroupId
        this.userGroupId = userGroupId
      })

  override fun saveAllRoleGroupToUserGroup(
    roleGroupIds: List<String>,
    userGroupId: String
  ): List<net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity> {
    val existingRoleGroups = ugrg.findAllRoleGroupIdByUserGroupId(userGroupId)
    val newRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity().apply {
        this.userGroupId = userGroupId
        roleGroupId = it
      }
    }
    return ugrg.saveAll(newRoleGroups)
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUserGroup(roleGroupId: String, userGroupId: String) =
    ugrg.deleteByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUserGroup(roleGroupIds: List<String>, userGroupId: String) =
    ugrg.deleteAllByRoleGroupIdInAndUserGroupId(roleGroupIds, userGroupId)

  override fun saveRoleToRoleGroup(roleId: String, roleGroupId: String): net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity? =
    rgr.findByRoleGroupIdAndRoleId(roleGroupId, roleId)
      ?: rgr.save(net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity().apply {
        this.roleGroupId = roleGroupId
        this.roleId = roleId
      })

  override fun saveAllRoleToRoleGroup(roleIds: List<String>, roleGroupId: String): List<net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity> {
    val existingRoles = rgr.findAllRoleIdByRoleGroupId(roleGroupId)
    val newRoles = roleIds.filterNot { existingRoles.contains(it) }.map {
      net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity().apply {
        this.roleGroupId = roleGroupId
        roleId = it
      }
    }
    return rgr.saveAll(newRoles)
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleFromRoleGroup(roleId: String, roleGroupId: String) =
    rgr.deleteByRoleGroupIdAndRoleId(roleGroupId, roleId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleFromRoleGroup(roleIds: List<String>, roleGroupId: String) = rgr.deleteAllByRoleIdInAndRoleGroupId(roleIds, roleGroupId)

  override fun savePermissionsToRole(permissionsId: String, roleId: String): net.yan100.compose.rds.entity.relationship.RolePermissionsEntity? =
    rp.findByRoleIdAndPermissionsId(roleId, permissionsId)
      ?: rp.save(net.yan100.compose.rds.entity.relationship.RolePermissionsEntity().apply {
        this.roleId = roleId
        this.permissionsId = permissionsId
      })

  override fun saveAllPermissionsToRole(
    permissionsIds: List<String>,
    roleId: String
  ): List<net.yan100.compose.rds.entity.relationship.RolePermissionsEntity> {
    val existingPermissions = rp.findAllPermissionsIdByRoleId(roleId)
    val newPermissions = permissionsIds.filterNot { existingPermissions.contains(it) }.map {
      net.yan100.compose.rds.entity.relationship.RolePermissionsEntity().apply {
        permissionsId = it
        this.roleId = roleId
      }
    }
    return rp.saveAll(newPermissions)
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun revokePermissionsFromRole(permissionsId: String, roleId: String) =
    rp.deleteByRoleIdAndPermissionsId(roleId, permissionsId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllPermissionsFromRole(permissionsIds: List<String>, roleId: String) =
    rp.deleteAllByPermissionsIdInAndRoleId(permissionsIds, roleId)
}
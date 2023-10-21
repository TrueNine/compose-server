package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.entity.relationship.RoleGroupRole
import net.yan100.compose.rds.entity.relationship.RolePermissions
import net.yan100.compose.rds.entity.relationship.UserGroupRoleGroup
import net.yan100.compose.rds.entity.relationship.UserRoleGroup
import net.yan100.compose.rds.repository.DeptRepo
import net.yan100.compose.rds.repository.FullRoleGroupEntityRepo
import net.yan100.compose.rds.repository.UserGroupRepo
import net.yan100.compose.rds.repository.UserRepo
import net.yan100.compose.rds.repository.relationship.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RbacAggregatorImpl(
  private val urg: UserRoleGroupRepo,
  private val ug: UserGroupRepo,
  private val userRepo: UserRepo,
  private val ugrg: UserGroupRoleGroupRepo,
  private val rgr: RoleGroupRoleRepo,
  private val rp: RolePermissionsRepo,
  private val rg: FullRoleGroupEntityRepo,
  private val dr: DeptRepo,
  private val udr: UserDeptRepo,
) : RbacAggregator {

  override fun findAllRoleNameByUserAccount(account: String): Set<String> =
    userRepo.findAllRoleNameByAccount(account)

  override fun findAllPermissionsNameByUserAccount(account: String): Set<String> =
    userRepo.findAllPermissionsNameByAccount(account)


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
    findAllSecurityNameByUserId(userRepo.findIdByAccount(account))

  override fun saveRoleGroupToUser(roleGroupId: String, userId: String): UserRoleGroup? =
    urg.findByUserIdAndRoleGroupId(userId, roleGroupId)
      ?: urg.save(UserRoleGroup().apply {
        this.userId = userId
        this.roleGroupId = roleGroupId
      })

  override fun saveAllRoleGroupToUser(roleGroupIds: List<String>, userId: String): List<UserRoleGroup> {
    val existingRoleGroups = urg.findAllRoleGroupIdByUserId(userId)
    val mewRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      UserRoleGroup().apply {
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

  override fun saveRoleGroupToUserGroup(roleGroupId: String, userGroupId: String): UserGroupRoleGroup? =
    ugrg.findByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)
      ?: ugrg.save(UserGroupRoleGroup().apply {
        this.roleGroupId = roleGroupId
        this.userGroupId = userGroupId
      })

  override fun saveAllRoleGroupToUserGroup(
    roleGroupIds: List<String>,
    userGroupId: String
  ): List<UserGroupRoleGroup> {
    val existingRoleGroups = ugrg.findAllRoleGroupIdByUserGroupId(userGroupId)
    val newRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      UserGroupRoleGroup().apply {
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

  override fun saveRoleToRoleGroup(roleId: String, roleGroupId: String): RoleGroupRole? =
    rgr.findByRoleGroupIdAndRoleId(roleGroupId, roleId)
      ?: rgr.save(RoleGroupRole().apply {
        this.roleGroupId = roleGroupId
        this.roleId = roleId
      })

  override fun saveAllRoleToRoleGroup(roleIds: List<String>, roleGroupId: String): List<RoleGroupRole> {
    val existingRoles = rgr.findAllRoleIdByRoleGroupId(roleGroupId)
    val newRoles = roleIds.filterNot { existingRoles.contains(it) }.map {
      RoleGroupRole().apply {
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

  override fun savePermissionsToRole(permissionsId: String, roleId: String): RolePermissions? =
    rp.findByRoleIdAndPermissionsId(roleId, permissionsId)
      ?: rp.save(RolePermissions().apply {
        this.roleId = roleId
        this.permissionsId = permissionsId
      })

  override fun saveAllPermissionsToRole(
    permissionsIds: List<String>,
    roleId: String
  ): List<RolePermissions> {
    val existingPermissions = rp.findAllPermissionsIdByRoleId(roleId)
    val newPermissions = permissionsIds.filterNot { existingPermissions.contains(it) }.map {
      RolePermissions().apply {
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

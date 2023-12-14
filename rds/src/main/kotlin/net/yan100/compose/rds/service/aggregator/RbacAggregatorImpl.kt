package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.rds.entities.relationship.RoleGroupRole
import net.yan100.compose.rds.entities.relationship.RolePermissions
import net.yan100.compose.rds.entities.relationship.UserRoleGroup
import net.yan100.compose.rds.repositories.relationship.IRoleGroupRoleRepo
import net.yan100.compose.rds.repositories.relationship.IRolePermissionsRepo
import net.yan100.compose.rds.repositories.relationship.IUserRoleGroupRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RbacAggregatorImpl(
  private val urg: IUserRoleGroupRepo,
  private val userRepo: net.yan100.compose.rds.repositories.IUsrRepo,
  private val rgr: IRoleGroupRoleRepo,
  private val rp: IRolePermissionsRepo,
  private val rg: net.yan100.compose.rds.repositories.FullRoleGroupEntityRepo,
) : IRbacAggregator {

  override fun findAllRoleNameByUserAccount(account: String): Set<String> =
    userRepo.findAllRoleNameByAccount(account)

  override fun findAllPermissionsNameByUserAccount(account: String): Set<String> =
    userRepo.findAllPermissionsNameByAccount(account)


  override fun findAllSecurityNameByUserId(userId: ReferenceId): Set<String> {
    // FIXME 待优化
    // 查询所有用户的角色组id
    val userRoleGroupIds = urg.findAllRoleGroupIdByUserId(userId)

    // 将之解包
    val allNames = with(
      userRoleGroupIds
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

  override fun saveRoleGroupToUser(roleGroupId: ReferenceId, userId: ReferenceId): UserRoleGroup? =
    urg.findByUserIdAndRoleGroupId(userId, roleGroupId)
      ?: urg.save(UserRoleGroup().apply {
        this.userId = userId
        this.roleGroupId = roleGroupId
      })

  override fun saveAllRoleGroupToUser(roleGroupIds: List<ReferenceId>, userId: ReferenceId): List<UserRoleGroup> {
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
  override fun revokeRoleGroupFromUser(roleGroupId: ReferenceId, userId: ReferenceId) {
    urg.deleteAllByRoleGroupIdAndUserId(roleGroupId, userId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUser(roleGroupIds: List<ReferenceId>, userId: ReferenceId) {
    urg.deleteAllByRoleGroupIdInAndUserId(roleGroupIds, userId)
  }

  override fun saveRoleToRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId): RoleGroupRole? =
    rgr.findByRoleGroupIdAndRoleId(roleGroupId, roleId)
      ?: rgr.save(RoleGroupRole().apply {
        this.roleGroupId = roleGroupId
        this.roleId = roleId
      })

  override fun saveAllRoleToRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId): List<RoleGroupRole> {
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
  override fun revokeRoleFromRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId) {
    rgr.deleteByRoleGroupIdAndRoleId(roleGroupId, roleId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleFromRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId) {
    rgr.deleteAllByRoleIdInAndRoleGroupId(roleIds, roleGroupId)
  }

  override fun savePermissionsToRole(permissionsId: ReferenceId, roleId: ReferenceId): RolePermissions? {
    return rp.findByRoleIdAndPermissionsId(roleId, permissionsId)
      ?: rp.save(RolePermissions().apply {
        this.roleId = roleId
        this.permissionsId = permissionsId
      })
  }

  override fun saveAllPermissionsToRole(
    permissionsIds: List<ReferenceId>,
    roleId: ReferenceId
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
  override fun revokePermissionsFromRole(permissionsId: ReferenceId, roleId: ReferenceId) {
    rp.deleteByRoleIdAndPermissionsId(roleId, permissionsId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllPermissionsFromRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId) {
    rp.deleteAllByPermissionsIdInAndRoleId(permissionsIds, roleId)
  }
}

package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.rds.repositories.relationship.IRoleGroupRoleRepo
import net.yan100.compose.rds.repositories.relationship.RolePermissionsRepo
import net.yan100.compose.rds.repositories.relationship.UserRoleGroupRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RbacAggregatorImpl(
  private val urg: UserRoleGroupRepo,
  private val userRepo: net.yan100.compose.rds.repositories.UserRepo,
  private val rgr: IRoleGroupRoleRepo,
  private val rp: RolePermissionsRepo,
  private val rg: net.yan100.compose.rds.repositories.FullRoleGroupEntityRepo,
) : IRbacAggregator {

  override fun findAllRoleNameByUserAccount(account: String): Set<String> =
    userRepo.findAllRoleNameByAccount(account)

  override fun findAllPermissionsNameByUserAccount(account: String): Set<String> =
    userRepo.findAllPermissionsNameByAccount(account)


  override fun findAllSecurityNameByUserId(userId: String): Set<String> {
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

  override fun saveRoleGroupToUser(roleGroupId: String, userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup? =
    urg.findByUserIdAndRoleGroupId(userId, roleGroupId)
      ?: urg.save(net.yan100.compose.rds.entities.relationship.UserRoleGroup().apply {
        this.userId = userId
        this.roleGroupId = roleGroupId
      })

  override fun saveAllRoleGroupToUser(roleGroupIds: List<String>, userId: String): List<net.yan100.compose.rds.entities.relationship.UserRoleGroup> {
    val existingRoleGroups = urg.findAllRoleGroupIdByUserId(userId)
    val mewRoleGroups = roleGroupIds.filterNot { existingRoleGroups.contains(it) }.map {
      net.yan100.compose.rds.entities.relationship.UserRoleGroup().apply {
        roleGroupId = it
        this.userId = userId
      }
    }
    return urg.saveAll(mewRoleGroups)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUser(roleGroupId: String, userId: String) = urg.deleteAllByRoleGroupIdAndUserId(roleGroupId, userId)

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUser(roleGroupIds: List<String>, userId: String) {
    urg.deleteAllByRoleGroupIdInAndUserId(roleGroupIds, userId)
  }

  override fun saveRoleToRoleGroup(roleId: String, roleGroupId: String): net.yan100.compose.rds.entities.relationship.RoleGroupRole? =
    rgr.findByRoleGroupIdAndRoleId(roleGroupId, roleId)
      ?: rgr.save(net.yan100.compose.rds.entities.relationship.RoleGroupRole().apply {
        this.roleGroupId = roleGroupId
        this.roleId = roleId
      })

  override fun saveAllRoleToRoleGroup(roleIds: List<String>, roleGroupId: String): List<net.yan100.compose.rds.entities.relationship.RoleGroupRole> {
    val existingRoles = rgr.findAllRoleIdByRoleGroupId(roleGroupId)
    val newRoles = roleIds.filterNot { existingRoles.contains(it) }.map {
      net.yan100.compose.rds.entities.relationship.RoleGroupRole().apply {
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

  override fun savePermissionsToRole(permissionsId: String, roleId: String): net.yan100.compose.rds.entities.relationship.RolePermissions? =
    rp.findByRoleIdAndPermissionsId(roleId, permissionsId)
      ?: rp.save(net.yan100.compose.rds.entities.relationship.RolePermissions().apply {
        this.roleId = roleId
        this.permissionsId = permissionsId
      })

  override fun saveAllPermissionsToRole(
    permissionsIds: List<String>,
    roleId: String
  ): List<net.yan100.compose.rds.entities.relationship.RolePermissions> {
    val existingPermissions = rp.findAllPermissionsIdByRoleId(roleId)
    val newPermissions = permissionsIds.filterNot { existingPermissions.contains(it) }.map {
      net.yan100.compose.rds.entities.relationship.RolePermissions().apply {
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

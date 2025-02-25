package net.yan100.compose.rds.crud.service.aggregator

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.crud.entities.jpa.RoleGroupRole
import net.yan100.compose.rds.crud.entities.jpa.RolePermissions
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup
import net.yan100.compose.rds.crud.repositories.jpa.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RbacAggregatorImpl(
  private val urg: IUserRoleGroupRepo,
  private val userRepo: IUserAccountRepo,
  private val rgr: IRoleGroupRoleRepo,
  private val rp: IRolePermissionsRepo,
  private val rg: IFullRoleGroupRepo,
) : IRbacAggregator {

  override fun fetchAllRoleNameByUserAccount(account: String): Set<String> =
    userRepo.findAllRoleNameByAccount(account)

  override fun findAllPermissionsNameByUserAccount(
    account: String
  ): Set<String> = userRepo.findAllPermissionsNameByAccount(account)

  override fun findAllSecurityNameByUserId(userId: RefId): Set<String> {
    // FIXME 待优化
    // 查询所有用户的角色组id
    val userRoleGroupIds = urg.findAllRoleGroupIdByUserId(userId)

    // 将之解包
    val allNames =
      with(userRoleGroupIds) {
        val roleGroups = rg.findAllById(this)
        val roleNames =
          roleGroups.map { it.roles }.flatten().map { "ROLE_${it.name}" }
        val permissionNames =
          roleGroups
            .asSequence()
            .map { it.roles }
            .flatten()
            .map { it.permissions }
            .flatten()
            .map { it.name }
            .toList()
        roleNames + permissionNames
      }
    return allNames.filterNotNull().toSet()
  }

  override fun findAllSecurityNameByAccount(account: String): Set<String> =
    findAllSecurityNameByUserId(userRepo.findIdByAccount(account)!!)

  override fun saveRoleGroupToUser(
    roleGroupId: RefId,
    userId: RefId,
  ): UserRoleGroup? =
    urg.findByUserIdAndRoleGroupId(userId, roleGroupId)
      ?: urg.save(
        UserRoleGroup().apply {
          this.userId = userId
          this.roleGroupId = roleGroupId
        }
      )

  override fun saveAllRoleGroupToUser(
    roleGroupIds: List<RefId>,
    userId: RefId,
  ): List<UserRoleGroup> {
    val existingRoleGroups = urg.findAllRoleGroupIdByUserId(userId)
    val mewRoleGroups =
      roleGroupIds
        .filterNot { existingRoleGroups.contains(it) }
        .map {
          UserRoleGroup().apply {
            roleGroupId = it
            this.userId = userId
          }
        }
    return urg.saveAll(mewRoleGroups)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupFromUser(roleGroupId: RefId, userId: RefId) {
    urg.deleteAllByRoleGroupIdAndUserId(roleGroupId, userId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleGroupFromUser(
    roleGroupIds: List<RefId>,
    userId: RefId,
  ) {
    urg.deleteAllByRoleGroupIdInAndUserId(roleGroupIds, userId)
  }

  override fun linkRoleToRoleGroup(
    roleId: RefId,
    roleGroupId: RefId,
  ): RoleGroupRole? =
    rgr.findByRoleGroupIdAndRoleId(roleGroupId, roleId)
      ?: rgr.save(
        RoleGroupRole().apply {
          this.roleGroupId = roleGroupId
          this.roleId = roleId
        }
      )

  override fun linkAllRoleToRoleGroup(
    roleIds: List<RefId>,
    roleGroupId: RefId,
  ): List<RoleGroupRole> {
    val existingRoles = rgr.findAllRoleIdByRoleGroupId(roleGroupId)
    val newRoles =
      roleIds
        .filterNot { existingRoles.contains(it) }
        .map {
          RoleGroupRole().apply {
            this.roleGroupId = roleGroupId
            roleId = it
          }
        }
    return rgr.saveAll(newRoles)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleFromRoleGroup(roleId: RefId, roleGroupId: RefId) {
    rgr.deleteByRoleGroupIdAndRoleId(roleGroupId, roleId)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeAllRoleFromRoleGroup(
    roleIds: List<RefId>,
    roleGroupId: RefId,
  ) {
    rgr.deleteAllByRoleIdInAndRoleGroupId(roleIds, roleGroupId)
  }

  override fun savePermissionsToRole(
    permissionsId: RefId,
    roleId: RefId,
  ): RolePermissions? {
    return rp.findByRoleIdAndPermissionsId(roleId, permissionsId)
      ?: rp.save(
        RolePermissions().apply {
          this.roleId = roleId
          this.permissionsId = permissionsId
        }
      )
  }

  override fun saveAllPermissionsToRole(
    permissionsIds: List<RefId>,
    roleId: RefId,
  ): List<RolePermissions> {
    val existingPermissions = rp.findAllPermissionsIdByRoleId(roleId)
    val newPermissions =
      permissionsIds
        .filterNot { existingPermissions.contains(it) }
        .map {
          RolePermissions().apply {
            permissionsId = it
            this.roleId = roleId
          }
        }
    return rp.saveAll(newPermissions)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokePermissionsFromRole(permissionsId: RefId, roleId: RefId) {
    rp.deleteByRoleIdAndPermissionsId(roleId, permissionsId)
  }

  @ACID
  override fun revokeAllPermissionsFromRole(
    permissionsIds: List<RefId>,
    roleId: RefId,
  ) {
    rp.deleteAllByPermissionsIdInAndRoleId(permissionsIds, roleId)
  }
}

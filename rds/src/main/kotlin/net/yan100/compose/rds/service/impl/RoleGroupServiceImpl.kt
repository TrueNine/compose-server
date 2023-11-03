package net.yan100.compose.rds.service.impl


import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.entity.RoleGroup
import net.yan100.compose.rds.entity.relationship.UserRoleGroup
import net.yan100.compose.rds.repository.RoleGroupRepo
import net.yan100.compose.rds.repository.relationship.UserRoleGroupRepo
import net.yan100.compose.rds.service.IRoleGroupService
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl(
  private val rgRepo: RoleGroupRepo,
  private val urRepo: UserRoleGroupRepo
) : IRoleGroupService, CrudService<RoleGroup>(rgRepo) {
  override fun assignRootToUser(userId: String): UserRoleGroup {
    return UserRoleGroup().apply {
      this.roleGroupId = DataBaseBasicFieldNames.Rbac.ROOT_ID_STR
      this.userId = userId
    }.let { urRepo.save(it) }
  }

  override fun assignPlainToUser(userId: String): UserRoleGroup {
    return UserRoleGroup().apply {
      this.roleGroupId = DataBaseBasicFieldNames.Rbac.USER_ID_STR
      this.userId = userId
    }.let { urRepo.save(it) }
  }

  override fun assignAdminToUser(userId: String): UserRoleGroup {
    return UserRoleGroup().apply {
      this.roleGroupId = DataBaseBasicFieldNames.Rbac.ADMIN_ID_STR
      this.userId = userId
    }.let { urRepo.save(it) }
  }
}

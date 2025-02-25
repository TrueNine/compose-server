package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.RoleGroup
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup
import net.yan100.compose.rds.crud.repositories.jpa.IRoleGroupRepo
import net.yan100.compose.rds.crud.repositories.jpa.IUserRoleGroupRepo
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl(
  private val rgRepo: IRoleGroupRepo,
  private val urRepo: IUserRoleGroupRepo,
) :
  net.yan100.compose.rds.crud.service.IRoleGroupService,
  ICrud<RoleGroup> by jpa(rgRepo) {
  override fun assignRootToUser(userId: RefId): UserRoleGroup {
    return UserRoleGroup()
      .apply {
        this.roleGroupId = IDbNames.Rbac.ROOT_ID
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }

  override fun assignPlainToUser(userId: RefId): UserRoleGroup {
    return UserRoleGroup()
      .apply {
        this.roleGroupId = IDbNames.Rbac.USER_ID
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }

  override fun assignAdminToUser(userId: RefId): UserRoleGroup {
    return UserRoleGroup()
      .apply {
        this.roleGroupId = IDbNames.Rbac.ADMIN_ID
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }
}

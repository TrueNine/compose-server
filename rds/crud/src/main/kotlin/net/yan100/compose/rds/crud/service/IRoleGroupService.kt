package net.yan100.compose.rds.crud.service

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.RoleGroup
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup

interface IRoleGroupService : ICrud<RoleGroup> {
  fun assignRootToUser(userId: RefId): UserRoleGroup

  fun assignPlainToUser(userId: RefId): UserRoleGroup

  fun assignAdminToUser(userId: RefId): UserRoleGroup
}

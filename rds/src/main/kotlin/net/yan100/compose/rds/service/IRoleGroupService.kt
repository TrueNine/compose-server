package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.entities.relationship.UserRoleGroup
import net.yan100.compose.rds.service.base.IService

interface IRoleGroupService : IService<RoleGroup> {
  fun assignRootToUser(userId: String): UserRoleGroup
  fun assignPlainToUser(userId: String): UserRoleGroup
  fun assignAdminToUser(userId: String): UserRoleGroup
}

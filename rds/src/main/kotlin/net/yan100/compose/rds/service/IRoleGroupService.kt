package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.service.base.IService

interface IRoleGroupService : IService<RoleGroup> {
  fun assignRootToUser(userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup
  fun assignPlainToUser(userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup
  fun assignAdminToUser(userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup
}

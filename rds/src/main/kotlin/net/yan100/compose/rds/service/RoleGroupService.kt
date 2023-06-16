package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.RoleGroup
import net.yan100.compose.rds.entity.relationship.UserRoleGroup

interface RoleGroupService : BaseService<RoleGroup> {
  fun assignRootToUser(userId: String): UserRoleGroup
  fun assignPlainToUser(userId: String): UserRoleGroup
  fun assignAdminToUser(userId: String): UserRoleGroup
}

package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.UserGroupRoleGroupEntity

/**
 * 用户组角色管理器
 */
interface UserGroupRoleGroupAggregator {
  fun assignRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long): UserGroupRoleGroupEntity?

  fun revokeRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long)
}

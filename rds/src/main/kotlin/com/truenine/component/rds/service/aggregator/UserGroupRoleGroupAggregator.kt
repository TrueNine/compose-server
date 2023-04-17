package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.UserGroupRoleGroupEntity

/**
 * 用户组角色管理器
 */
interface UserGroupRoleGroupAggregator {
  fun saveRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long): UserGroupRoleGroupEntity?

  fun revokeRoleGroupFromUserGroup(roleGroupId: Long, userGroupId: Long)
}

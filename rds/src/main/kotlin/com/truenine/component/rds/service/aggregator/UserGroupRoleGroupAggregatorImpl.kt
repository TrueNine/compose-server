package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import com.truenine.component.rds.repository.UserGroupRoleGroupRepository
import com.truenine.component.rds.service.RoleGroupService
import com.truenine.component.rds.service.UserGroupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserGroupRoleGroupAggregatorImpl(
  private val ugs: UserGroupService,
  private val roleGroupService: RoleGroupService,
  private val ugrgr: UserGroupRoleGroupRepository
) : UserGroupRoleGroupAggregator {

  override fun assignRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long) = ugrgr.save(UserGroupRoleGroupEntity().apply {
    this.userGroupId = userGroupId
    this.roleGroupId = roleGroupId
  })

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupToUserGroup(roleGroupId: Long, userGroupId: Long) = ugrgr.deleteAllByUserGroupIdAndRoleGroupId(userGroupId, roleGroupId)
}

package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import com.truenine.component.rds.repository.UserRoleGroupRepository
import com.truenine.component.rds.service.RoleGroupService
import com.truenine.component.rds.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRoleGroupAggregatorImpl(
  private val us: UserService,
  private val rs: RoleGroupService,
  private val rgrr: UserRoleGroupRepository
) : UserRoleGroupAggregator {

}

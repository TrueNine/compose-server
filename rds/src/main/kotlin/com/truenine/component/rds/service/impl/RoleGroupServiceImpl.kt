package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.RoleGroupEntity
import com.truenine.component.rds.repository.RoleGroupRepository
import com.truenine.component.rds.service.RoleGroupService
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl
  (repo: RoleGroupRepository) :
  RoleGroupService, BaseServiceImpl<RoleGroupEntity>(repo) {
}

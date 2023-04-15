package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.RoleEntity
import com.truenine.component.rds.repository.RoleRepository
import com.truenine.component.rds.service.RoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl
  (repo: RoleRepository) : RoleService, BaseServiceImpl<RoleEntity>(repo) {
}

package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.RoleEntity
import net.yan100.compose.rds.repository.RoleRepository
import net.yan100.compose.rds.service.RoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl
  (repo: RoleRepository) : RoleService, BaseServiceImpl<RoleEntity>(repo)

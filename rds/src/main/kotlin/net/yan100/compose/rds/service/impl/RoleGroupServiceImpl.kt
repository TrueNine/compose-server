package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.RoleGroupEntity
import net.yan100.compose.rds.repository.RoleGroupRepository
import net.yan100.compose.rds.service.RoleGroupService
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl
  (repo: RoleGroupRepository) :
  RoleGroupService, BaseServiceImpl<RoleGroupEntity>(repo)

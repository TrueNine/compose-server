package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.entities.Role
import net.yan100.compose.rds.repositories.RoleRepository
import net.yan100.compose.rds.service.IRoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl
  (repo: RoleRepository) : IRoleService, CrudService<Role>(repo)

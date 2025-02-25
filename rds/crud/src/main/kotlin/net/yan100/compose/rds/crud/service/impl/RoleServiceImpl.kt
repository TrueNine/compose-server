package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.Role
import net.yan100.compose.rds.crud.repositories.jpa.IRoleRepo
import net.yan100.compose.rds.crud.service.IRoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(repo: IRoleRepo) : IRoleService, ICrud<Role> by jpa(repo)

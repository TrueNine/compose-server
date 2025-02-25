package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.Permissions
import net.yan100.compose.rds.crud.repositories.jpa.IPermissionsRepo
import net.yan100.compose.rds.crud.service.IPermissionsService
import org.springframework.stereotype.Service

@Service
class PermissionsServiceImpl(repo: IPermissionsRepo) :
  IPermissionsService, ICrud<Permissions> by jpa(repo)

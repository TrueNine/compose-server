package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.entities.Permissions
import net.yan100.compose.rds.repositories.IPermissionsRepo
import net.yan100.compose.rds.service.IPermissionsService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class PermissionsServiceImpl(
  repo: IPermissionsRepo
) : IPermissionsService, CrudService<Permissions>(repo)

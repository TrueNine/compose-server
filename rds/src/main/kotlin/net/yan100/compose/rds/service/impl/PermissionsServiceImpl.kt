package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.entity.Permissions
import net.yan100.compose.rds.repository.PermissionsRepo
import net.yan100.compose.rds.service.IPermissionsService
import org.springframework.stereotype.Service

@Service
class PermissionsServiceImpl(
  repo: PermissionsRepo
) : IPermissionsService, CrudService<Permissions>(repo)

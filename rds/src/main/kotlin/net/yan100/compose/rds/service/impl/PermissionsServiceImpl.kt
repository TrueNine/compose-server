package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.Permissions
import net.yan100.compose.rds.repository.PermissionsRepo
import net.yan100.compose.rds.service.PermissionsService
import org.springframework.stereotype.Service

@Service
class PermissionsServiceImpl(
  repo: PermissionsRepo
) : PermissionsService, BaseServiceImpl<Permissions>(repo)

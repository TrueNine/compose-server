package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.PermissionsEntity
import com.truenine.component.rds.repository.PermissionsRepository
import com.truenine.component.rds.service.PermissionsService
import org.springframework.stereotype.Service

@Service
class PermissionsServiceImpl(
  repo: PermissionsRepository
) : PermissionsService, BaseServiceImpl<PermissionsEntity>(repo) {
}

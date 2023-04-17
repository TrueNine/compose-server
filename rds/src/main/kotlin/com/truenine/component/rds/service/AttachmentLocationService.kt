package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.entity.AttachmentLocationEntity

interface AttachmentLocationService : BaseService<AttachmentLocationEntity> {
  fun findByBaseUrl(baseUrl: String): AttachmentLocationEntity?
}

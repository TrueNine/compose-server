package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.AttachmentLocationEntity

interface AttachmentLocationService : BaseService<AttachmentLocationEntity> {
  fun findByBaseUrl(baseUrl: String): AttachmentLocationEntity?
}

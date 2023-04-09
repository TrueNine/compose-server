package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.AttachmentLocationEntity
import org.springframework.stereotype.Repository

@Repository
interface AttachmentLocationRepo : BaseRepo<AttachmentLocationEntity, String> {
  fun findByBaseUrl(baseUrl: String): AttachmentLocationEntity?
}

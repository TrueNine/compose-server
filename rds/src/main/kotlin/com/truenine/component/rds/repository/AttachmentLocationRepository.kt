package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.AttachmentLocationEntity
import org.springframework.stereotype.Repository

@Repository
interface AttachmentLocationRepository : BaseRepository<AttachmentLocationEntity> {
  fun findByBaseUrl(baseUrl: String): AttachmentLocationEntity?
  fun findByBaseUrlStartingWith(baseUrl: String): AttachmentLocationEntity?
}

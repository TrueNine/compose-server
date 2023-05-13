package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.AttachmentLocationEntity
import org.springframework.stereotype.Repository

@Repository
interface AttachmentLocationRepository : BaseRepository<AttachmentLocationEntity> {
  fun findByBaseUrlContaining(baseUrl: String): AttachmentLocationEntity?
  fun findByBaseUrlStartingWith(baseUrl: String): AttachmentLocationEntity?
}

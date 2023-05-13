package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.AttachmentLocationEntity
import net.yan100.compose.rds.repository.AttachmentLocationRepository
import net.yan100.compose.rds.service.AttachmentLocationService
import org.springframework.stereotype.Service

@Service
class AttachmentLocationServiceImpl(
  private val alRepo: AttachmentLocationRepository
) : BaseServiceImpl<AttachmentLocationEntity>(alRepo), AttachmentLocationService {
  override fun findByBaseUrl(baseUrl: String): AttachmentLocationEntity? =
    alRepo.findByBaseUrlContaining(
      baseUrl
        .replace("http://", "")
        .replace("https://", "")
    )
}

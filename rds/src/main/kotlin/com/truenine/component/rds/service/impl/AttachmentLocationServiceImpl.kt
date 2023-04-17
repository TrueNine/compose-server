package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.AttachmentLocationEntity
import com.truenine.component.rds.repository.AttachmentLocationRepository
import com.truenine.component.rds.service.AttachmentLocationService
import org.springframework.stereotype.Service

@Service
class AttachmentLocationServiceImpl(
  private val alRepo: AttachmentLocationRepository
) :
  BaseServiceImpl<AttachmentLocationEntity>(alRepo),
  AttachmentLocationService {

  override fun findByBaseUrl(baseUrl: String): AttachmentLocationEntity? = alRepo.findByBaseUrl(baseUrl)

}

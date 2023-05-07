package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.base.PagedRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.repository.AttachmentRepository
import com.truenine.component.rds.service.AttachmentService
import com.truenine.component.rds.util.page
import com.truenine.component.rds.util.result
import org.springframework.stereotype.Service

@Service
class AttachmentServiceImpl(
  private val repo: AttachmentRepository
) : AttachmentService, BaseServiceImpl<AttachmentEntity>(repo) {
  override fun findFullUrlById(id: Long): String? =
    repo.findFullPathById(id)

  override fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: PagedRequestParam): PagedResponseResult<String> =
    repo.findAllFullUrlByMetaNameStartingWith(metaName, page.page).result
}

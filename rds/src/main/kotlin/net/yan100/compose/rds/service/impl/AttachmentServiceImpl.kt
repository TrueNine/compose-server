package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.AttachmentEntity
import net.yan100.compose.rds.repository.AttachmentRepository
import net.yan100.compose.rds.service.AttachmentService
import net.yan100.compose.rds.util.page
import net.yan100.compose.rds.util.result
import org.springframework.stereotype.Service

@Service
class AttachmentServiceImpl(
  private val repo: AttachmentRepository
) : AttachmentService, BaseServiceImpl<AttachmentEntity>(repo) {
  override fun findFullUrlById(id: Long): String? =
    repo.findFullPathById(id)

  override fun findAllFullUrlByMetaNameStartingWith(
    metaName: String,
    page: net.yan100.compose.rds.base.PagedRequestParam
  ): net.yan100.compose.rds.base.PagedResponseResult<String> =
    repo.findAllFullUrlByMetaNameStartingWith(metaName, page.page).result
}
package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.AttachmentEntity
import net.yan100.compose.rds.util.PagedWrapper


interface AttachmentService : BaseService<AttachmentEntity> {
  fun existsByBaseUrl(baseUrl: String): Boolean
  fun findByBaseUrl(baseUrl: String): AttachmentEntity?
  fun findFullUrlById(id: Long): String?
  fun findAllFullUrlByMetaNameStartingWith(
    metaName: String,
    page: net.yan100.compose.rds.base.PagedRequestParam = PagedWrapper.DEFAULT_MAX
  ): net.yan100.compose.rds.base.PagedResponseResult<String>
}

package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.util.PagedWrapper


interface AttachmentService : BaseService<Attachment> {
  fun existsByBaseUrl(baseUrl: String): Boolean
  fun findByBaseUrl(baseUrl: String): Attachment?
  fun findFullUrlById(id: String): String?
  fun findAllFullUrlByMetaNameStartingWith(
    metaName: String,
    page: net.yan100.compose.rds.base.PagedRequestParam = PagedWrapper.DEFAULT_MAX
  ): net.yan100.compose.rds.base.PagedResponseResult<String>
}

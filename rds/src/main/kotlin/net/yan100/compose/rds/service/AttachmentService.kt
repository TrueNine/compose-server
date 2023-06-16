package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.entity.LinkedAttachment
import net.yan100.compose.rds.util.PagedWrapper
import net.yan100.compose.rds.util.Pq
import net.yan100.compose.rds.util.Pr
import net.yan100.compose.rds.util.Pw


interface AttachmentService : BaseService<Attachment> {
  fun existsByBaseUrl(baseUrl: String): Boolean
  fun findByBaseUrl(baseUrl: String): Attachment?
  fun findFullUrlById(id: String): String?
  fun findAllFullUrlByMetaNameStartingWith(
    metaName: String,
    page: net.yan100.compose.rds.base.PagedRequestParam = PagedWrapper.DEFAULT_MAX
  ): Pr<String>

  /**
   * ## 根据 baseurl 查询其下的所有文件地址
   */
  fun findAllLinkedAttachmentByParentBaseUrl(baseUrl: String, page: Pq = Pw.DEFAULT_MAX): Pr<LinkedAttachment>

  /**
   * ## 根据 baseurl 查询其下的所有文件
   */
  fun findAllByParentBaseUrl(baseUrl: String, page: Pq = Pw.DEFAULT_MAX): Pr<Attachment>

  /**
   * ## 根据 id 查新附件的url
   */
  fun findLinkedById(id: String):LinkedAttachment?
  fun findAllLinkedById(ids: List<String>):List<LinkedAttachment>
}

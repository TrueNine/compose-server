package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.entity.LinkedAttachment
import net.yan100.compose.rds.repository.AttachmentRepo
import net.yan100.compose.rds.repository.LinkedAttachmentRepo
import net.yan100.compose.rds.service.AttachmentService
import net.yan100.compose.rds.util.Pq
import net.yan100.compose.rds.util.Pr
import net.yan100.compose.rds.util.page
import net.yan100.compose.rds.util.result
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AttachmentServiceImpl(
  private val repo: AttachmentRepo,
  private val linkedRepo: LinkedAttachmentRepo
) : AttachmentService, BaseServiceImpl<Attachment>(repo) {
  override fun existsByBaseUrl(baseUrl: String): Boolean {
    return repo.existsByBaseUrl(baseUrl)
  }

  override fun findByBaseUrl(baseUrl: String): Attachment? {
    return repo.findFirstByBaseUrl(baseUrl)
  }

  override fun findFullUrlById(id: String): String? {
    return repo.findFullPathById(id)
  }

  override fun findAllByParentBaseUrl(baseUrl: String, page: Pq): Pr<Attachment> {
    return repo.findAllByParentBaseUrl(baseUrl, page.page).result
  }

  override fun findLinkedById(id: String): LinkedAttachment? {
    return linkedRepo.findByIdOrNull(id)
  }

  override fun findAllLinkedById(ids: List<String>): List<LinkedAttachment> {
    return linkedRepo.findAllById(ids)
  }

  override fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: Pq): Pr<String> {
    return repo.findAllFullUrlByMetaNameStartingWith(metaName, page.page).result
  }

  override fun findAllLinkedAttachmentByParentBaseUrl(baseUrl: String, page: Pq): Pr<LinkedAttachment> {
    return linkedRepo.findAllByParentBaseUrl(baseUrl, page.page).result
  }
}

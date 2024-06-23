/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.service.impl

import net.yan100.compose.core.alias.Pq
import net.yan100.compose.core.alias.Pr
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.core.util.page
import net.yan100.compose.rds.core.util.result
import net.yan100.compose.rds.entities.attachment.Attachment
import net.yan100.compose.rds.entities.attachment.LinkedAttachment
import net.yan100.compose.rds.repositories.IAttachmentRepo
import net.yan100.compose.rds.repositories.ILinkedAttachmentRepo
import net.yan100.compose.rds.service.IAttachmentService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AttachmentServiceImpl(
  private val repo: IAttachmentRepo,
  private val linkedRepo: ILinkedAttachmentRepo,
) : IAttachmentService, CrudService<Attachment>(repo) {
  override fun existsByBaseUrl(baseUrl: String): Boolean {
    return repo.existsByBaseUrl(baseUrl)
  }

  override fun fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(
    baseUrl: String,
    baseUri: String,
  ): Attachment {
    return findByBaseUrlAndBaseUri(baseUrl, baseUri)
      ?: save(
        Attachment().apply {
          attType = AttachmentTyping.BASE_URL
          this.baseUrl = baseUrl
          this.baseUri = baseUri
        }
      )
  }

  override fun findByBaseUrl(baseUrl: String): Attachment? {
    return repo.findFirstByBaseUrl(baseUrl)
  }

  override fun findByBaseUrlAndBaseUri(baseUrl: String, baseUri: String): Attachment? {
    return repo.findFirstByBaseUrlAndBaseUri(baseUrl, baseUri)
  }

  override fun findAllByBaseUrlIn(baseUrls: List<String>): List<Attachment> {
    return repo.findAllByBaseUrlIn(baseUrls)
  }

  override fun findAllByBaseUrlInAndBaseUriIn(
    baseUrls: List<String>,
    baseUris: List<String>,
  ): List<Attachment> {
    TODO("Not yet implemented")
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

  override fun findMetaNameById(id: String): String? {
    return repo.findMetaNameById(id)
  }

  override fun findSaveNameById(id: String): String? {
    return repo.findSaveNameById(id)
  }

  override fun findAllLinkedAttachmentByParentBaseUrl(
    baseUrl: String,
    page: Pq,
  ): Pr<LinkedAttachment> {
    return linkedRepo.findAllByParentBaseUrl(baseUrl, page.page).result
  }
}

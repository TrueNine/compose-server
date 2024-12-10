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
package net.yan100.compose.rds.crud.service.impl


import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.RefId
import net.yan100.compose.core.domain.IReadableAttachment
import net.yan100.compose.core.typing.MimeTypes
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.core.toPageable
import net.yan100.compose.rds.core.toPr
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.crud.entities.jpa.Attachment
import net.yan100.compose.rds.crud.entities.jpa.LinkedAttachment
import net.yan100.compose.rds.crud.repositories.jpa.IAttachmentRepo
import net.yan100.compose.rds.crud.repositories.jpa.ILinkedAttachmentRepo
import net.yan100.compose.rds.crud.service.IAttachmentService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class AttachmentServiceImpl(
  private val attRepo: IAttachmentRepo,
  private val linkedRepo: ILinkedAttachmentRepo
) : IAttachmentService, ICrud<Attachment> by jpa(attRepo),
  IAttachmentRepo by attRepo {
  @ACID
  override fun fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(baseUrl: String, baseUri: String): Attachment {
    return fetchByBaseUrlAndBaseUri(baseUrl, baseUri)
      ?: post(
        Attachment().apply {
          attType = AttachmentTyping.BASE_URL
          this.baseUrl = baseUrl
          this.baseUri = baseUri
        }
      )
  }

  override fun fetchByBaseUrl(baseUrl: String): Attachment? {
    return attRepo.findFirstByBaseUrl(baseUrl)
  }

  override fun fetchByBaseUrlAndBaseUri(baseUrl: String, baseUri: String): Attachment? {
    return attRepo.findFirstByBaseUrlAndBaseUri(baseUrl, baseUri)
  }

  override fun fetchFullUrlById(id: RefId): String? {
    return attRepo.findFullPathById(id)
  }

  override fun fetchAllByParentBaseUrl(baseUrl: String, page: Pq): Pr<Attachment> {
    return attRepo.findAllByParentBaseUrl(baseUrl, page.toPageable()).toPr()
  }

  override fun fetchLinkedById(id: RefId): LinkedAttachment? {
    return linkedRepo.findByIdOrNull(id)
  }

  override fun fetchAllLinkedById(ids: List<RefId>): List<LinkedAttachment> {
    return linkedRepo.findAllById(ids)
  }

  override fun fetchAllFullUrlByMetaNameStartingWith(metaName: String, page: Pq): Pr<String> {
    return attRepo.findAllFullUrlByMetaNameStartingWith(metaName, page.toPageable()).toPr()
  }

  override fun fetchAllLinkedAttachmentByParentBaseUrl(baseUrl: String, page: Pq): Pr<LinkedAttachment> {
    return linkedRepo.findAllByParentBaseUrl(baseUrl, page.toPageable()).toPr()
  }

  @ACID
  override fun recordUpload(
    readableAttachment: IReadableAttachment,
    saveFn: (readableAttachment: IReadableAttachment) -> IAttachmentService.PostDto
  ): Attachment? {
    val saveFile = saveFn(readableAttachment)
    val location = fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(saveFile.baseUrl!!, saveFile.baseUri!!)
    // 构建一个新附件对象保存并返回
    val att =
      Attachment().apply {
        // 将之于根路径连接
        urlId = location.id
        saveName = saveFile.saveName
        metaName = readableAttachment.name
        size = readableAttachment.size
        mimeType = readableAttachment.mimeType ?: MimeTypes.BINARY.value
        attType = AttachmentTyping.ATTACHMENT
      }
    // 重新进行赋值
    return post(att)
  }

  @ACID
  override fun recordUpload(stream: InputStream, saveFn: (stream: InputStream) -> IAttachmentService.PostDto): Attachment? {
    val saveFile = saveFn(stream)
    val location = fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(saveFile.baseUrl!!, saveFile.baseUri!!)
    val allBytes = stream.readAllBytes()
    return Attachment()
      .apply {
        urlId = location.id
        saveName = saveFile.saveName
        metaName = saveFile.metaName
        size = allBytes.size.toLong()
        mimeType = saveFile.mimeType?.value ?: MimeTypes.BINARY.value
        attType = AttachmentTyping.ATTACHMENT
      }
      .let { post(it) }
  }

  @ACID
  override fun recordUpload(
    readableAttachments: List<IReadableAttachment>,
    saveFn: (att: IReadableAttachment) -> IAttachmentService.PostDto
  ): List<Attachment> {
    val saved = readableAttachments.map { saveFn(it) to it }
    val baseUrls =
      findAllByBaseUrlInAndBaseUriIn(saved.map { it.first.baseUrl!! }, saved.map { it.first.baseUri!! }).associateBy { it.baseUrl!! to it.baseUri!! }
    return saved
      .map {
        val baseUrl =
          baseUrls[it.first.baseUrl to it.first.baseUri] ?: post(
            Attachment().apply {
              attType = AttachmentTyping.BASE_URL
              baseUrl = it.first.baseUrl
              baseUri = it.first.baseUri
            }
          )
        Attachment().apply {
          // 将之于根路径连接
          urlId = baseUrl.id
          saveName = it.first.saveName
          metaName = it.second.name
          size = it.second.size
          mimeType = it.second.mimeType
          attType = AttachmentTyping.ATTACHMENT
        }
      }.let { postAll(it) }
  }
}

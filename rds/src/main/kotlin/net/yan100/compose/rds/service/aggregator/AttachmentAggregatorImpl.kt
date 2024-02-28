/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.core.extensionfunctions.hasText
import net.yan100.compose.core.typing.http.MediaTypes
import net.yan100.compose.rds.entities.Attachment
import net.yan100.compose.rds.models.req.PostAttachmentReq
import net.yan100.compose.rds.service.IAttachmentService
import net.yan100.compose.rds.typing.AttachmentTyping
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

/** 附件聚合实现 */
@Service
class AttachmentAggregatorImpl(
  private val aService: IAttachmentService,
) : IAttachmentAggregator {

  @Transactional(rollbackFor = [Exception::class])
  override fun uploadAttachment(
    file: MultipartFile,
    @Valid saveFileCallback: (file: MultipartFile) -> @Valid PostAttachmentReq
  ): Attachment? {
    val saveFile = saveFileCallback(file)
    // 如果 此条url 不存在，则保存一个新的 url
    val location =
      aService.findByBaseUrlAndBaseUri(saveFile.baseUrl!!, saveFile.baseUri!!)
        ?: aService.save(
          Attachment().apply {
            this.attType = AttachmentTyping.BASE_URL
            this.baseUrl = saveFile.baseUrl
            this.baseUri = saveFile.baseUri
          }
        )
    checkNotNull(location.id) { "没有保存的url" }
    // 构建一个新附件对象保存并返回
    val att =
      Attachment().apply {
        // 将之于根路径连接
        urlId = location.id
        saveName = saveFile.saveName
        metaName = if (file.originalFilename.hasText()) file.originalFilename else file.name
        size = file.size
        mimeType = file.contentType ?: MediaTypes.BINARY.value
        attType = AttachmentTyping.ATTACHMENT
      }
    // 重新进行赋值
    return aService.save(att)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun uploadAttachments(
    files: List<MultipartFile>,
    saveFileCallback: (file: MultipartFile) -> PostAttachmentReq
  ): List<Attachment> {
    val saved = files.map { saveFileCallback(it) to it }
    val baseUrls =
      aService
        .findAllByBaseUrlInAndBaseUriIn(
          saved.map { it.first.baseUrl!! },
          saved.map { it.first.baseUri!! }
        )
        .associateBy { it.baseUrl!! to it.baseUri!! }

    return saved
      .map {
        val baseUrl =
          baseUrls[it.first.baseUrl to it.first.baseUri]
            ?: aService.save(
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
          metaName =
            if (it.second.originalFilename.hasText()) it.second.originalFilename else it.second.name
          size = it.second.size
          mimeType = it.second.contentType ?: MediaTypes.BINARY.value
          attType = AttachmentTyping.ATTACHMENT
        }
      }
      .let { aService.saveAll(it) }
  }
}

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
package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.core.hasText
import net.yan100.compose.core.typing.MediaTypes
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.entities.Attachment
import net.yan100.compose.rds.service.IAttachmentService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

/** 附件聚合实现 */
@Service
class AttachmentAggregatorImpl(private val aService: IAttachmentService) : IAttachmentAggregator {

  @ACID
  override fun recordUpload(file: MultipartFile, @Valid saveFileCallback: (file: MultipartFile) -> @Valid IAttachmentAggregator.PostDto): Attachment? {
    val saveFile = saveFileCallback(file)
    val location = aService.fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(saveFile.baseUrl!!, saveFile.baseUri!!)
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
    return aService.post(att)
  }

  override fun recordUpload(stream: InputStream, req: (stream: InputStream) -> IAttachmentAggregator.PostDescDto): Attachment? {
    val saveFile = req(stream)
    val location = aService.fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(saveFile.baseUrl!!, saveFile.baseUri!!)
    val allBytes = stream.readAllBytes()
    return Attachment()
      .apply {
        urlId = location.id
        saveName = saveFile.saveName
        metaName = saveFile.metaName
        size = allBytes.size.toLong()
        mimeType = saveFile.mimeType?.value ?: MediaTypes.BINARY.value
        attType = AttachmentTyping.ATTACHMENT
      }
      .let { aService.post(it) }
  }

  @ACID
  override fun recordUploads(files: List<MultipartFile>, saveFileCallback: (file: MultipartFile) -> IAttachmentAggregator.PostDto): List<Attachment> {
    val saved = files.map { saveFileCallback(it) to it }
    val baseUrls =
      aService.findAllByBaseUrlInAndBaseUriIn(saved.map { it.first.baseUrl!! }, saved.map { it.first.baseUri!! }).associateBy { it.baseUrl!! to it.baseUri!! }

    return saved
      .map {
        val baseUrl =
          baseUrls[it.first.baseUrl to it.first.baseUri]
            ?: aService.post(
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
          metaName = if (it.second.originalFilename.hasText()) it.second.originalFilename else it.second.name
          size = it.second.size
          mimeType = it.second.contentType ?: MediaTypes.BINARY.value
          attType = AttachmentTyping.ATTACHMENT
        }
      }
      .let { aService.postAll(it) }
  }
}

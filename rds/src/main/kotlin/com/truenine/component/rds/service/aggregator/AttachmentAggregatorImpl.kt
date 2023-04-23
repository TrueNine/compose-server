package com.truenine.component.rds.service.aggregator

import com.truenine.component.core.http.MediaTypes
import com.truenine.component.core.lang.hasText
import com.truenine.component.core.lang.slf4j
import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.entity.AttachmentLocationEntity
import com.truenine.component.rds.models.SaveAttachmentModel
import com.truenine.component.rds.service.AttachmentLocationService
import com.truenine.component.rds.service.AttachmentService
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

/**
 * 附件聚合实现
 */
@Service
class AttachmentAggregatorImpl(
  private val aService: AttachmentService,
  private val alService: AttachmentLocationService
) : AttachmentAggregator {

  override fun uploadAttachment(file: MultipartFile, @Valid saveFileCallback: () -> SaveAttachmentModel): AttachmentEntity? {
    val saveFile = saveFileCallback()
    // 如果 此条url 不存在，则保存一个新的 url
    val location = alService.findByBaseUrl(saveFile.baseUrl)
      ?: alService.save(AttachmentLocationEntity().apply {
        baseUrl = saveFile.baseUrl
        type = saveFile.storageType
        name = "URL:\$${LocalDateTime.now()}"
        log.debug("保存一个新的 附件地址 = {}", this)
      })!!

    // 构建一个新附件对象保存并返回
    val att = AttachmentEntity().apply {
      saveName = saveFile.saveName
      metaName = if (file.originalFilename.hasText()) file.originalFilename else file.name
      size = file.size
      mimeType = file.contentType ?: MediaTypes.BINARY.media()
      attachmentLocationId = location.id!!
      this.location = location
    }
    // 重新进行赋值
    return aService.save(att)
  }

  override fun getFullUrl(attachment: AttachmentEntity): String? = attachment.fullPath

  companion object {
    @JvmStatic
    private val log = slf4j(this::class)
  }
}

package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.entity.AttachmentEntity
import net.yan100.compose.rds.entity.AttachmentLocationEntity
import net.yan100.compose.rds.models.request.PostAttachmentRequestParam
import net.yan100.compose.rds.service.AttachmentLocationService
import net.yan100.compose.rds.service.AttachmentService
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

  override fun uploadAttachment(file: MultipartFile, @Valid saveFileCallback: () -> PostAttachmentRequestParam): AttachmentEntity? {
    val saveFile = saveFileCallback()
    // 如果 此条url 不存在，则保存一个新的 url
    val location = alService.findByBaseUrl(saveFile.baseUrl)
      ?: alService.save(AttachmentLocationEntity().apply {
        baseUrl = saveFile.baseUrl
        type = saveFile.storageType
        name = "URL:\$${LocalDateTime.now()}"
        log.debug("保存一个新的 附件地址 = {}", this)
      })
    // 构建一个新附件对象保存并返回
    val att = AttachmentEntity().apply {
      saveName = saveFile.saveName
      metaName = if (file.originalFilename.hasText()) file.originalFilename else file.name
      size = file.size
      mimeType = file.contentType ?: net.yan100.compose.core.http.MediaTypes.BINARY.media()
      attachmentLocationId = location.id!!
    }
    // 重新进行赋值
    return aService.save(att)
  }

  companion object {
    @JvmStatic
    private val log = slf4j(this::class)
  }
}

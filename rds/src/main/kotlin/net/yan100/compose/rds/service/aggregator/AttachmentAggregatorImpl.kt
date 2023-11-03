package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.models.req.PostAttachmentReq
import net.yan100.compose.rds.service.IAttachmentService
import net.yan100.compose.rds.typing.AttachmentTyping
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * 附件聚合实现
 */
@Service
class AttachmentAggregatorImpl(
    private val aService: IAttachmentService,
) : AttachmentAggregator {

  override fun uploadAttachment(file: MultipartFile, @Valid saveFileCallback: () -> @Valid PostAttachmentReq): Attachment? {
    val saveFile = saveFileCallback()
    // 如果 此条url 不存在，则保存一个新的 url
    val location = aService.findByBaseUrl(saveFile.baseUrl!!) ?: aService.save(Attachment().apply {
      this.attType = AttachmentTyping.BASE_URL
      this.baseUrl = saveFile.baseUrl
    })
    checkNotNull(location.id) { "没有保存的url" }
    // 构建一个新附件对象保存并返回
    val att = Attachment().apply {
      // 将之于根路径连接
      urlId = location.id
      saveName = saveFile.saveName
      metaName = if (file.originalFilename.hasText()) file.originalFilename else file.name
      size = file.size
      mimeType = file.contentType ?: net.yan100.compose.core.http.MediaTypes.BINARY.getValue()
      attType = AttachmentTyping.ATTACHMENT
    }
    // 重新进行赋值
    return aService.save(att)
  }
}

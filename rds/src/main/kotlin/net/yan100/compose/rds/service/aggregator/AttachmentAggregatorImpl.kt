package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.rds.entities.Attachment
import net.yan100.compose.rds.models.req.PostAttachmentReq
import net.yan100.compose.rds.service.IAttachmentService
import net.yan100.compose.rds.typing.AttachmentTyping
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

/**
 * 附件聚合实现
 */
@Service
class AttachmentAggregatorImpl(
  private val aService: IAttachmentService,
) : IAttachmentAggregator {

  @Transactional(rollbackFor = [Exception::class])
  override fun uploadAttachment(file: MultipartFile, @Valid saveFileCallback: (file: MultipartFile) -> @Valid PostAttachmentReq): Attachment? {
    val saveFile = saveFileCallback(file)
    // 如果 此条url 不存在，则保存一个新的 url
    val location = aService.findByBaseUrl(saveFile.baseUrl!!) ?: aService.save(
      Attachment().apply {
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

  @Transactional(rollbackFor = [Exception::class])
  override fun uploadAttachments(files: List<MultipartFile>, saveFileCallback: (file: MultipartFile) -> PostAttachmentReq): List<Attachment> {
    val saved = files.map {
      saveFileCallback(it) to it
    }
    val baseUrls = aService.findAllByBaseUrlIn(saved.map { it.first.baseUrl!! }).associateBy { it.baseUrl!! }

    return saved.map {
      val baseUrl = baseUrls[it.first.baseUrl] ?: aService.save(Attachment().apply {
        this.attType = AttachmentTyping.BASE_URL
        this.baseUrl = it.first.baseUrl
      })
      Attachment().apply {
        // 将之于根路径连接
        urlId = baseUrl.id
        saveName = it.first.saveName
        metaName = if (it.second.originalFilename.hasText()) it.second.originalFilename else it.second.name
        size = it.second.size
        mimeType = it.second.contentType ?: net.yan100.compose.core.http.MediaTypes.BINARY.getValue()
        attType = AttachmentTyping.ATTACHMENT
      }
    }.let { aService.saveAll(it) }
  }
}

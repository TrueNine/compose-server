package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.models.request.PostAttachmentRequestParam
import org.springframework.web.multipart.MultipartFile

/**
 * 附件服务接口聚合器
 */
interface AttachmentAggregator {
  fun uploadAttachment(file: MultipartFile, saveFileCallback: () -> PostAttachmentRequestParam): Attachment?
}

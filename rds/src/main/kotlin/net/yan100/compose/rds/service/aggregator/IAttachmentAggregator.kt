package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.models.req.PostAttachmentReq
import org.springframework.web.multipart.MultipartFile

/**
 * 附件服务接口聚合器
 */
interface IAttachmentAggregator {
  fun uploadAttachment(file: MultipartFile, saveFileCallback: () -> PostAttachmentReq): Attachment?
}

package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.models.request.PostAttachmentRequestParam
import org.springframework.web.multipart.MultipartFile

/**
 * 附件服务接口聚合器
 */
interface AttachmentAggregator {
  fun uploadAttachment(file: MultipartFile, saveFileCallback: () -> PostAttachmentRequestParam): AttachmentEntity?
}

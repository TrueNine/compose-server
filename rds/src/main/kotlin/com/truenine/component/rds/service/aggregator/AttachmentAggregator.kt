package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.models.SaveAttachmentModel
import org.springframework.web.multipart.MultipartFile

/**
 * 附件服务接口聚合器
 */
interface AttachmentAggregator {
  fun getFullUrl(attachment: AttachmentEntity): String?
  fun uploadAttachment(file: MultipartFile, saveFileCallback: () -> SaveAttachmentModel): AttachmentEntity?
}

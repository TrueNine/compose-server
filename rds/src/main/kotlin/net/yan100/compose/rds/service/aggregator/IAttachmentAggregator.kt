package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.entities.Attachment
import net.yan100.compose.rds.models.req.PostAttachmentReq
import org.springframework.web.multipart.MultipartFile

/**
 * 附件服务接口聚合器
 */
interface IAttachmentAggregator {
    fun uploadAttachment(file: MultipartFile, saveFileCallback: (file: MultipartFile) -> PostAttachmentReq): Attachment?
    fun uploadAttachments(files: List<MultipartFile>, saveFileCallback: (file: MultipartFile) -> PostAttachmentReq): List<Attachment>
}

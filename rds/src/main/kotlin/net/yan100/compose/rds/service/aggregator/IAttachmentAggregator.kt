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

import java.io.InputStream
import net.yan100.compose.rds.entities.attachment.Attachment
import net.yan100.compose.rds.models.req.PostAttachmentDescriptionDto
import net.yan100.compose.rds.models.req.PostAttachmentDto
import org.springframework.web.multipart.MultipartFile

/**
 * # 附件服务聚合接口
 *
 * @author TrueNine
 * @since 2024-03-20
 */
interface IAttachmentAggregator {
  fun uploadAttachment(file: MultipartFile, saveFileCallback: (file: MultipartFile) -> PostAttachmentDto): Attachment?

  fun uploadAttachment(stream: InputStream, req: (stream: InputStream) -> PostAttachmentDescriptionDto): Attachment?

  fun uploadAttachments(files: List<MultipartFile>, saveFileCallback: (file: MultipartFile) -> PostAttachmentDto): List<Attachment>
}

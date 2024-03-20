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
package net.yan100.compose.rds.models.req

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.http.MediaTypes

@Schema(title = "记录文件")
open class PostAttachmentDto {
    @get:Schema(title = "存储的 uri")
    var baseUri: String? = null

    @get:Schema(title = "存储的url")
    var baseUrl: String? = null

    @get:Schema(title = "保存后的名称")
    var saveName: String? = null
}

@Schema(title = "记录文件（流辅助描述信息）")
class PostAttachmentDescriptionDto : PostAttachmentDto() {
    @Schema(title = "附件大小")
    var size: Long? = null

    @Schema(title = "存根在系统内的描述符", description = "通常为序列值")
    var metaName: String? = null

    @Schema(title = "附件类型", description = "通常在默认情况下为 二进制文件")
    var mimeType: MediaTypes? = null
}
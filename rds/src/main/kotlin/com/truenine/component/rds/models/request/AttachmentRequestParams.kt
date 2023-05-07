package com.truenine.component.rds.models.request

import com.truenine.component.rds.typing.AttachmentStorageTyping
import io.swagger.v3.oas.annotations.media.Schema


@Schema(title = "记录文件")
interface PostAttachmentRequestParam {
  @get:Schema(title = "存储的url")
  var baseUrl: String

  @get:Schema(title = "保存后的名称")
  var saveName: String

  @get:Schema(title = "存储类别")
  var storageType: AttachmentStorageTyping
}

package net.yan100.compose.rds.models.request

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.rds.typing.AttachmentTyping


@Schema(title = "记录文件")
interface PostAttachmentRequestParam {
  @get:Schema(title = "存储的url")
  var baseUrl: String

  @get:Schema(title = "保存后的名称")
  var saveName: String
}

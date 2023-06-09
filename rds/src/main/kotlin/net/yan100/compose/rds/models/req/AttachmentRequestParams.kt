package net.yan100.compose.rds.models.req

import io.swagger.v3.oas.annotations.media.Schema


@Schema(title = "记录文件")
class PostAttachmentReq {

  @get:Schema(title = "存储的url")
  var baseUrl: String? = null

  @get:Schema(title = "保存后的名称")
  var saveName: String? = null
}

package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.AttachmentTypingConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.typing.AttachmentTyping

@MetaDef
interface SuperAttachment : IJpaEntity {
  /** 保存前的名称 */
  var metaName: String?

  /** 根路径 */
  var baseUrl: String?

  /** baseUri 基础路径 */
  var baseUri: String?

  /** 保存后的名称 */
  var saveName: String?

  /** 根路径名称 */
  var urlName: String?

  /** 根路径描述 */
  var urlDoc: String?

  /** 附件类型 */
  @get:Convert(converter = AttachmentTypingConverter::class)
  var attType: AttachmentTyping

  var size: Long?

  /** 媒体类型 */
  var mimeType: String?

  var urlId: RefId?
}

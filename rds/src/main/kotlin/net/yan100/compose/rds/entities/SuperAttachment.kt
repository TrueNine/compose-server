package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.RefId
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.AttachmentTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AttachmentTyping

@MetaDef
@MappedSuperclass
abstract class SuperAttachment : IEntity() {
  /** 保存前的名称 */
  @get:Schema(title = "保存前的名称")
  abstract var metaName: String?

  /** 根路径 */
  @get:Schema(title = "根路径")
  abstract var baseUrl: String?

  /** baseUri 基础路径 */
  @get:Schema(title = "基础 URI")
  abstract var baseUri: String?

  /** 保存后的名称 */
  @get:Schema(title = "保存后的名称")
  abstract var saveName: String?

  /** 根路径名称 */
  @get:Schema(title = "根路径名称")
  abstract var urlName: String?

  /** 根路径描述 */
  @get:Schema(title = "根路径描述")
  abstract var urlDoc: String?

  /** 附件类型 */
  @get:Schema(title = "附件类型（附件、根路径）")
  @get:Convert(converter = AttachmentTypingConverter::class)
  abstract var attType: AttachmentTyping

  @get:Schema(title = "附件大小")
  abstract var size: Long?

  /** 媒体类型 */
  @get:Schema(title = "媒体类型")
  abstract var mimeType: String?

  @get:Schema(title = "自连接 urlId")
  abstract var urlId: RefId?
}

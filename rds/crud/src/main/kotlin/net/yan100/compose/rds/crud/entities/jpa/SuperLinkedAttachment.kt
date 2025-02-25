package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.meta.annotations.orm.MetaFormula
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.crud.converters.AttachmentTypingConverter
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.JOIN
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE

/**
 * # 附件全路径实体
 * - 这是一个特殊实体
 * - 序列化到前端，只会出现全路径
 * - 只能用于查询，无法插入
 */
@MetaName("attachment")
@MetaDef(shadow = true)
interface SuperLinkedAttachment : IJpaEntity {
  var mimeType: String
  var baseUrl: String?
  var baseUri: String?
  var urlId: RefId
  var saveName: String
  var metaName: String

  @get:Convert(converter = AttachmentTypingConverter::class)
  var attType: AttachmentTyping

  @get:ManyToOne(fetch = EAGER)
  @get:JoinColumn(
    name = "url_id",
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @get:Fetch(JOIN)
  @get:NotFound(action = IGNORE)
  var base: Attachment

  @MetaFormula
  @get:Transient
  val uri: String
    get() {
      val uri =
        base.baseUri?.let {
          if (it.startsWith("/")) it.slice(1..it.length) else it
        } ?: ""
      val lastUri = if (uri.endsWith("/")) uri else "$uri/"
      val name = saveName
      return "$lastUri$name"
    }

  @get:Transient
  @MetaFormula
  val url: String
    get() {
      val based =
        base.baseUrl?.let { if (it.endsWith("/")) it else "$it/" } ?: ""
      return "$based$uri"
    }
}

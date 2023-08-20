package net.yan100.compose.rds.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.criteria.Predicate
import net.yan100.compose.rds.base.BaseEntity
import net.yan100.compose.rds.converters.typing.AttachmentTypingConverter
import net.yan100.compose.rds.typing.AttachmentTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.JOIN
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE
import org.springframework.data.jpa.domain.Specification


@MappedSuperclass
open class SuperAttachment : BaseEntity() {

  /**
   * 保存前的名称
   */
  @Nullable
  @Schema(title = "保存前的名称")
  @Column(name = META_NAME)
  open var metaName: String? = null

  /**
   * 根路径
   */
  @Nullable
  @Schema(title = "根路径")
  @Column(name = BASE_URL)
  open var baseUrl: String? = null

  /**
   * 保存后的名称
   */
  @Nullable
  @Column(name = SAVE_NAME)
  @Schema(title = "保存后的名称")
  open var saveName: String? = null

  /**
   * 根路径名称
   */
  @Nullable
  @Column(name = URL_NAME)
  @Schema(title = "根路径名称")
  open var urlName: String? = null

  /**
   * 根路径描述
   */
  @Nullable
  @Column(name = URL_DOC)
  @Schema(title = "根路径描述")
  open var urlDoc: String? = null

  /**
   * 附件类型
   */
  @Nullable
  @Column(name = ATT_TYPE)
  @Schema(title = "附件类型（附件、根路径）")
  open var attType: AttachmentTyping? = AttachmentTyping.ATTACHMENT

  @Nullable
  @Column(name = SIZE)
  @Schema(title = "附件大小")
  open var size: Long? = null

  /**
   * 媒体类型
   */
  @Nullable
  @Column(name = MIME_TYPE)
  @Schema(title = "媒体类型")
  open var mimeType: String? = null

  @JsonIgnore
  @Nullable
  @Column(name = URL_ID)
  @Schema(title = "自连接 urlId")
  open var urlId: String? = null


  companion object {
    const val TABLE_NAME = "attachment"

    const val URL_ID = "url_id"
    const val META_NAME = "meta_name"
    const val SAVE_NAME = "save_name"
    const val BASE_URL = "base_url"
    const val URL_NAME = "url_name"
    const val URL_DOC = "url_doc"
    const val ATT_TYPE = "att_type"
    const val SIZE = "size"
    const val MIME_TYPE = "mime_type"
  }
}

/**
 * 新附件类型
 *
 * @author TrueNine
 * @since 2023-05-29
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "附件")
@Table(name = SuperAttachment.TABLE_NAME)
open class Attachment : SuperAttachment()

/**
 * # 附件全路径实体
 *
 * - 这是一个特殊实体
 * - 序列化到前端，只会出现全路径
 * - 只能用于查询，无法插入
 */
@Entity
@Schema(title = "附件")
@Table(name = SuperAttachment.TABLE_NAME)
class LinkedAttachment : BaseEntity() {

  @JsonIgnore
  @Column(name = SuperAttachment.BASE_URL, insertable = false, updatable = false)
  var baseUrl: String? = null

  @JsonIgnore
  @Column(name = SuperAttachment.URL_ID, insertable = false, updatable = false)
  var urlId: String? = null

  @JsonIgnore
  @Column(name = SuperAttachment.SAVE_NAME, insertable = false, updatable = false)
  var saveName: String? = null

  @Schema(title = "原始名称")
  @Column(name = SuperAttachment.META_NAME, insertable = false, updatable = false)
  var metaName: String? = null

  @JsonIgnore
  @Column(name = SuperAttachment.ATT_TYPE, insertable = false, updatable = false)
  @Convert(converter = AttachmentTypingConverter::class)
  var attType: AttachmentTyping? = null

  @JsonIgnore
  @ManyToOne(fetch = EAGER)
  @JoinColumn(
    name = SuperAttachment.URL_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @Fetch(JOIN)
  @NotFound(action = IGNORE)
  var base: Attachment? = null

  @get:Transient
  val url: String
    get() {
      val based = base?.baseUrl?.let { if (it.endsWith("/")) it else "$it/" } ?: ""
      val name = metaName ?: ""
      return "$based$name"
    }
}

fun <T> LinkedAttachment.toSpec(): Specification<T> = Specification { root, query, builder ->
  val p = mutableListOf<Predicate>()
  attType?.let { p += builder.equal(root.get<AttachmentTyping>(SuperAttachment.ATT_TYPE), attType) }

  builder.and(*p.toTypedArray())
}

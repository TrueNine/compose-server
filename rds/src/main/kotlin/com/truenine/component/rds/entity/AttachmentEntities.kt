package com.truenine.component.rds.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.truenine.component.rds.base.BaseEntity
import com.truenine.component.rds.converters.typing.AttachmentStorageTypingConverter
import com.truenine.component.rds.typing.AttachmentStorageTyping
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import java.io.Serial


@MappedSuperclass
open class SuperAttachmentEntity : BaseEntity() {
  @Schema(title = "附件地址 id")
  @Column(name = ATTACHMENT_LOCATION_ID, nullable = false)
  open var attachmentLocationId: Long? = null

  /**
   * 原始名称
   */
  @Schema(title = "原始名称")
  @Column(name = META_NAME, nullable = false)
  open var metaName: String? = null

  /**
   * 存储后名称
   */
  @Nullable
  @JsonIgnore
  @Schema(title = "存储后名称", hidden = true)
  @Column(name = SAVE_NAME)
  open var saveName: String? = null

  /**
   * 文件大小
   */
  @Nullable
  @Schema(title = "文件大小")
  @Column(name = SIZE)
  open var size: Long? = null

  /**
   * MIME TYPE
   */
  @Nullable
  @Schema(title = "MIME TYPE")
  @Column(name = MIME_TYPE)
  open var mimeType: String? = null

  companion object {
    const val TABLE_NAME = "attachment"
    const val ATTACHMENT_LOCATION_ID = "attachment_location_id"
    const val META_NAME = "meta_name"
    const val SAVE_NAME = "save_name"
    const val SIZE = "size"
    const val MIME_TYPE = "mime_type"

    @Serial
    private const val serialVersionUID = 1L
  }
}

/**
 * 文件
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "附件")
@Table(name = SuperAttachmentEntity.TABLE_NAME)
open class AttachmentEntity : SuperAttachmentEntity()

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "全附件")
@Table(name = SuperAttachmentEntity.TABLE_NAME)
open class AllAttachmentEntity : SuperAttachmentEntity() {

  /**
   * URL
   */
  @Nullable
  @ManyToOne
  @Schema(title = "URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JoinColumn(
    name = ATTACHMENT_LOCATION_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var location: AllAttachmentLocationEntity? = null

  @get:Transient
  @get:Schema(title = "全路径")
  open var fullPath: String
    /**
     * @return 全路径
     */
    get() = location?.let { "${it.baseUrl}/${saveName}" } ?: "/$saveName"
    set(_) {}
}


@MappedSuperclass
open class SuperAttachmentLocationEntity : BaseEntity() {
  /**
   * 基本url
   */
  @Schema(title = "基本url")
  @Column(name = BASE_URL, nullable = false)
  open var baseUrl: String? = null

  /**
   * 资源路径名称
   */
  @Schema(title = "资源路径名称")
  @Column(name = NAME, nullable = false)
  open var name: String? = null

  /**
   * 资源路径描述
   */
  @Schema(title = "资源路径描述")
  @Column(name = DOC)
  @Nullable
  open var doc: String? = null

  /**
   * 存储类别
   */
  @Schema(title = "存储类别", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Column(name = TYPE, nullable = false)
  @Convert(converter = AttachmentStorageTypingConverter::class)
  open var type = AttachmentStorageTyping.NATIVE

  companion object {
    const val TABLE_NAME = "attachment_location"
    const val BASE_URL = "base_url"
    const val NAME = "name"
    const val DOC = "doc"
    const val TYPE = "type"

    @Serial
    private const val serialVersionUID = 1L
  }
}


/**
 * 文件地址
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "附件地址")
@Table(name = SuperAttachmentLocationEntity.TABLE_NAME)
open class AttachmentLocationEntity : SuperAttachmentLocationEntity() {
  @Nullable
  @get:Transient
  @set:Transient
  @get:Schema(title = "是否为远程存储")
  open var rn: Boolean?
    get() = AttachmentStorageTyping.REMOTE == type
    set(storageRnType) {
      type = if (storageRnType!!) AttachmentStorageTyping.REMOTE else AttachmentStorageTyping.NATIVE
    }
}

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "文件全返回结果")
@Table(name = SuperAttachmentLocationEntity.TABLE_NAME)
open class AllAttachmentLocationEntity : SuperAttachmentLocationEntity()

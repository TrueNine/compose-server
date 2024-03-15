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
package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.criteria.Predicate
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.long
import net.yan100.compose.rds.Jc
import net.yan100.compose.rds.Mto
import net.yan100.compose.rds.converters.AttachmentTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AttachmentTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.JOIN
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE
import org.springframework.data.jpa.domain.Specification

@MappedSuperclass
abstract class SuperAttachment : IEntity() {
  companion object {
    const val TABLE_NAME = "attachment"

    const val URL_ID = "url_id"
    const val META_NAME = "meta_name"
    const val SAVE_NAME = "save_name"
    const val BASE_URL = "base_url"
    const val BASE_URI = "base_uri"
    const val URL_NAME = "url_name"
    const val URL_DOC = "url_doc"
    const val ATT_TYPE = "att_type"
    const val SIZE = "size"
    const val MIME_TYPE = "mime_type"
  }

  /** 保存前的名称 */
  @Nullable @Schema(title = "保存前的名称") @Column(name = META_NAME) var metaName: String? = null

  /** 根路径 */
  @Nullable @Schema(title = "根路径") @Column(name = BASE_URL) var baseUrl: String? = null

  @Schema(title = "基础 URI") @Column(name = BASE_URI) var baseUri: String? = null

  /** 保存后的名称 */
  @Nullable @Column(name = SAVE_NAME) @Schema(title = "保存后的名称") var saveName: String? = null

  /** 根路径名称 */
  @Nullable @Column(name = URL_NAME) @Schema(title = "根路径名称") var urlName: String? = null

  /** 根路径描述 */
  @Nullable @Column(name = URL_DOC) @Schema(title = "根路径描述") var urlDoc: String? = null

  /** 附件类型 */
  @Nullable
  @Column(name = ATT_TYPE)
  @Schema(title = "附件类型（附件、根路径）")
  lateinit var attType: AttachmentTyping

  @Nullable @Column(name = SIZE) @Schema(title = "附件大小") var size: long? = null

  /** 媒体类型 */
  @Nullable @Column(name = MIME_TYPE) @Schema(title = "媒体类型") var mimeType: String? = null

  @JsonIgnore @Nullable @Column(name = URL_ID) @Schema(title = "自连接 urlId") var urlId: RefId? = null
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
class Attachment : SuperAttachment()

/**
 * # 附件全路径实体
 * - 这是一个特殊实体
 * - 序列化到前端，只会出现全路径
 * - 只能用于查询，无法插入
 */
@Entity
@Schema(title = "组合查询附件")
@Table(name = SuperAttachment.TABLE_NAME)
class LinkedAttachment : IEntity() {

  @Schema(title = "媒体类型")
  @Column(name = SuperAttachment.MIME_TYPE, insertable = false, updatable = false)
  lateinit var mimeType: String

  @JsonIgnore
  @Column(name = SuperAttachment.BASE_URL, insertable = false, updatable = false)
  lateinit var baseUrl: String

  @JsonIgnore
  @Column(name = SuperAttachment.BASE_URI, insertable = false, updatable = false)
  var baseUri: String? = null

  @JsonIgnore
  @Column(name = SuperAttachment.URL_ID, insertable = false, updatable = false)
  lateinit var urlId: String

  @Schema(title = "保存后的名称")
  @Column(name = SuperAttachment.SAVE_NAME, insertable = false, updatable = false)
  lateinit var saveName: String

  @Schema(title = "原始名称")
  @Column(name = SuperAttachment.META_NAME, insertable = false, updatable = false)
  lateinit var metaName: String

  @JsonIgnore
  @Column(name = SuperAttachment.ATT_TYPE, insertable = false, updatable = false)
  @Convert(converter = AttachmentTypingConverter::class)
  lateinit var attType: AttachmentTyping

  @JsonIgnore
  @Mto(fetch = EAGER)
  @Jc(
    name = SuperAttachment.URL_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @Fetch(JOIN)
  @NotFound(action = IGNORE)
  lateinit var base: Attachment

  @get:JsonIgnore
  @get:Transient
  val uri: String
    get() {
      val uri = base.baseUri?.let { if (it.startsWith("/")) it.slice(1..it.length) else it } ?: ""
      val lastUri = if (uri.endsWith("/")) uri else "$uri/"
      val name = saveName
      return "$lastUri$name"
    }

  @get:Transient
  val url: String
    get() {
      val based = base.baseUrl?.let { if (it.endsWith("/")) it else "$it/" } ?: ""
      return "$based$uri"
    }
}

fun <T> LinkedAttachment.toSpec(): Specification<T> = Specification { root, _, builder ->
  val p = mutableListOf<Predicate>()
  attType.let { p += builder.equal(root.get<AttachmentTyping>(SuperAttachment.ATT_TYPE), attType) }
  builder.and(*p.toTypedArray())
}

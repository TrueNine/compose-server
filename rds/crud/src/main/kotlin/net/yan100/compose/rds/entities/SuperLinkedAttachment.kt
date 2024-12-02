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
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.rds.converters.AttachmentTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.AttachmentTyping
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
@Schema(title = "组合查询附件")
@MappedSuperclass
@MetaName("attachment")
@MetaDef(shadow = true)
abstract class SuperLinkedAttachment : IEntity {
  @get:Schema(title = "媒体类型")
  abstract var mimeType: String

  @get:JsonIgnore
  abstract var baseUrl: String

  @get:JsonIgnore
  abstract var baseUri: String?

  @get:JsonIgnore
  abstract var urlId: String

  @get:Schema(title = "保存后的名称")
  abstract var saveName: String

  @get:Schema(title = "原始名称")
  abstract var metaName: String

  @get:JsonIgnore
  @get:Convert(converter = AttachmentTypingConverter::class)
  abstract var attType: AttachmentTyping

  @ManyToOne(fetch = EAGER)
  @JoinColumn(
    name = Attachment.URL_ID,
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(NO_CONSTRAINT), insertable = false, updatable = false
  )
  @Fetch(JOIN)
  @NotFound(action = IGNORE)
  open lateinit var base: Attachment

  @get:JsonIgnore
  @get:Transient
  open val uri: String
    get() {
      val uri = base.baseUri?.let { if (it.startsWith("/")) it.slice(1..it.length) else it } ?: ""
      val lastUri = if (uri.endsWith("/")) uri else "$uri/"
      val name = saveName
      return "$lastUri$name"
    }

  @get:Transient
  open val url: String
    get() {
      val based = base.baseUrl?.let { if (it.endsWith("/")) it else "$it/" } ?: ""
      return "$based$uri"
    }
}


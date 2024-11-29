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
package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.Id
import net.yan100.compose.core.bool
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.domain.AbstractLazyInitScope
import net.yan100.compose.core.domain.ISensitivity
import net.yan100.compose.rds.core.listener.BizCodeInsertListener
import net.yan100.compose.rds.core.listener.SnowflakeIdInsertListener
import org.hibernate.Hibernate
import org.springframework.data.domain.Persistable
import java.io.Serial
import java.io.Serializable

/**
 * ## JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@MappedSuperclass
@Schema(title = "顶级任意抽象类")
@EntityListeners(
  BizCodeInsertListener::class,
  SnowflakeIdInsertListener::class,
)
abstract class IAnyEntity : ISensitivity,
  Persistable<Id>,
  IExtensionDefineScope,
  IEnhanceEntity, Serializable,
  AbstractLazyInitScope() {
  companion object {
    @Serial
    const val serialVersionUID = 1L

    /** 主键 */
    @kotlin.jvm.Transient
    const val ID = IDbNames.ID
  }

  /** id */
  @jakarta.persistence.Id
  @Column(name = ID)
  @Schema(
    title = ID,
    name = ID,
    description = "仅在更新时需要携带 ID",
    example = "7173087959242248192",
    examples = ["7001234523405", "7001234523441"],
    required = false,
    requiredMode = Schema.RequiredMode.NOT_REQUIRED,
  )
  @Suppress("ALL")
  private var id: Id? = null
    @Transient @JsonIgnore @JvmName("_\$\$_get_kotlin_internal_primary_id") get() = field ?: ""

  @Schema(required = false, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  open fun setId(id: String) {
    this.id = id
  }

  override fun getId(): Id = this.id!!

  override fun equals(other: Any?): Boolean {
    return if (this === other) true
    else if (null == other || Hibernate.getClass(this) != Hibernate.getClass(other)) false
    else !isNew && id == (other as IAnyEntity).id
  }

  override fun toString(): String {
    return if (isNew) "AnyEntity(New)" else "AnyEntity(id=$id)"
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  open fun toNewEntity() {
    id = null
  }

  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  final override fun isNew(): Boolean {
    return null == id || "" == id || "null" == id
  }

  @kotlin.jvm.Transient
  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  private var _sensed: Boolean = false

  final override fun recordChangedSensitiveData() {
    _sensed = true
  }

  @kotlin.jvm.Transient
  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  final override val isChangedToSensitiveData: bool = _sensed

  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    require(!_sensed) { "数据已经脱敏，无需重复执行" }
    this.id = null
    recordChangedSensitiveData()
  }
}

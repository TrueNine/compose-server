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
import jakarta.validation.constraints.NotBlank
import java.io.Serial
import net.yan100.compose.core.alias.Id
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.core.models.sensitive.ISensitivity
import net.yan100.compose.depend.jvalid.group.DeleteGroup
import net.yan100.compose.depend.jvalid.group.PatchGroup
import net.yan100.compose.depend.jvalid.group.PutGroup
import net.yan100.compose.rds.core.listener.BizCodeInsertListener
import net.yan100.compose.rds.core.listener.PreSaveDeleteReferenceListener
import net.yan100.compose.rds.core.listener.SnowflakeIdInsertListener
import net.yan100.compose.rds.core.listener.TableRowDeletePersistenceListener
import net.yan100.compose.rds.core.models.JpaPagedRequestParam
import org.hibernate.Hibernate
import org.jetbrains.annotations.ApiStatus.Experimental
import org.springframework.data.domain.Persistable

/**
 * ## JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@MappedSuperclass
@Schema(title = "顶级任意抽象类")
@EntityListeners(
  TableRowDeletePersistenceListener::class,
  BizCodeInsertListener::class,
  SnowflakeIdInsertListener::class,
  PreSaveDeleteReferenceListener::class,
)
class IAnyEntity : ISensitivity, Persistable<Id>, IDatabaseDefineEntity, IEnhanceEntity, IPageableEntity {

  companion object {
    /** 主键 */
    const val ID = DataBaseBasicFieldNames.ID

    @Serial private val serialVersionUID = 1L
  }

  /** ## 是否需要脱敏处理 */
  @JsonIgnore @kotlin.jvm.Transient @Transient @Experimental private var ____sensitive: Boolean = false

  /** id */
  @jakarta.persistence.Id
  @Column(name = DataBaseBasicFieldNames.ID)
  @NotBlank(groups = [PutGroup::class, PatchGroup::class, DeleteGroup::class], message = "在修改数据时，需携带数据 id")
  @Schema(
    title = ID,
    name = ID,
    description = "仅在更新时需要携带 ID",
    example = "7173087959242248192",
    examples = ["7001234523405", "7001234523441"],
    required = false,
    requiredMode = Schema.RequiredMode.NOT_REQUIRED,
  )
  private var id: Id? = null
    @Transient @JsonIgnore @JvmName("setKotlinInternalId") set
    @Transient @JsonIgnore @JvmName("getKotlinInternalId") get() = field ?: ""

  @Schema(required = false, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  fun setId(id: String) {
    this.id = id
  }

  @JsonIgnore @kotlin.jvm.Transient @Transient private final val ____page = JpaPagedRequestParam()

  override fun getId(): Id = this.id.orEmpty()

  override var pageSize: Int?
    @Transient get() = ____page.pageSize
    @Transient
    set(value) {
      ____page.pageSize = value
    }

  override var offset: Int?
    @Transient get() = ____page.offset
    @Transient
    set(value) {
      ____page.offset = value
    }

  override var unPage: Boolean?
    @Transient get() = ____page.unPage
    @Transient
    set(value) {
      ____page.unPage = value
    }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    return "" != id && id != null && "null" != id && id == (other as IAnyEntity).id
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  fun asNew() {
    this.id = null
  }

  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  fun withToString(superString: String, vararg properties: Pair<String, Any?>): String = buildString {
    append(superString)
    append("[")

    properties.forEach {
      append(it.first)
      append("=")
      append(it.second)
      append(",")
    }
    removeSuffix(",")
    append("]")
  }

  @Transient
  @JsonIgnore
  @Schema(hidden = true)
  override fun isNew(): Boolean {
    return null == id || "" == id || "null" == id
  }

  protected final fun <T> Companion.late(): IDatabaseDefineEntity.LateInitNonNullBasicValue<T> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  protected final fun Companion.boolLate(): IDatabaseDefineEntity.LateInitNonNullBasicValue<Boolean> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  protected final fun Companion.byteLate(): IDatabaseDefineEntity.LateInitNonNullBasicValue<Byte> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  protected final fun Companion.intLate(): IDatabaseDefineEntity.LateInitNonNullBasicValue<Int> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  protected final fun Companion.longLate(): IDatabaseDefineEntity.LateInitNonNullBasicValue<Long> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  protected final fun Companion.floatLate(): IDatabaseDefineEntity.LateInitNonNullBasicValue<Float> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  protected final fun Companion.doubleLate(): IDatabaseDefineEntity.LateInitNonNullBasicValue<Double> = IDatabaseDefineEntity.LateInitNonNullBasicValue()

  @kotlin.jvm.Transient @Transient @JsonIgnore @Schema(hidden = true) private var sensed: Boolean = false

  override fun sensitive() {
    check(!sensed) { "数据已经脱敏，无需重复执行" }
    super.sensitive()
  }
}

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
import jakarta.persistence.*
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.datetime
import net.yan100.compose.core.i64
import net.yan100.compose.rds.core.domain.PersistenceAuditData
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

/**
 * jpa顶级抽象类
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@MappedSuperclass
@Schema(title = "顶级抽象类")
abstract class IEntity : IAnyEntity() {
  companion object {
    @kotlin.jvm.Transient
    const val RLV = IDbNames.ROW_LOCK_VERSION

    @kotlin.jvm.Transient
    const val LDF = IDbNames.LOGIC_DELETE_FLAG

    @kotlin.jvm.Transient
    const val CRD = IDbNames.CREATE_ROW_DATETIME

    @kotlin.jvm.Transient
    const val MRD = IDbNames.MODIFY_ROW_DATETIME
  }

  /**
   * ## 当前数据的审计数据
   */
  @get:JsonIgnore
  @get:Transient
  @get:Schema(hidden = true)
  val dbEntityAuditData: PersistenceAuditData?
    get() = if (isNew) null
    else PersistenceAuditData(
      dbEntityShadowRemoveTag, dbEntityRowLockVersion, id, dbEntityCreatedDatetime, dbEntityLastModifyDatetime
    )


  /** 乐观锁版本 */
  @Version
  @JsonIgnore
  @Column(name = RLV)
  @Schema(hidden = true, title = "乐观锁版本")
  @Basic(fetch = FetchType.LAZY)
  @Deprecated(message = "不建议直接调用", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("dbEntityRowLockVersion"))

  var rlv: i64? = null

  @Suppress("DEPRECATION_ERROR")
  val dbEntityRowLockVersion: i64?
    @Schema(title = "字段乐观锁版本号") @Transient @JsonIgnore get() = rlv

  @CreatedDate
  @JsonIgnore
  @Basic(fetch = FetchType.LAZY)
  @Deprecated(message = "不建议直接调用", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("dbEntityCreatedDatetime"))
  @Schema(title = "表行创建时间")
  @Column(name = CRD)
  var crd: datetime? = null

  @Suppress("DEPRECATION_ERROR")
  val dbEntityCreatedDatetime: datetime?
    @Schema(title = "字段创建时间") @Transient @JsonIgnore get() = crd

  @JsonIgnore
  @LastModifiedDate
  @Basic(fetch = FetchType.LAZY)
  @Deprecated(message = "不建议直接调用", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("dbEntityLastModifyDatetime"))
  @Schema(title = "表行修改时间")
  @Column(name = MRD)
  var mrd: datetime? = null

  @Suppress("DEPRECATION_ERROR")
  val dbEntityLastModifyDatetime: datetime?
    @Schema(title = "字段的修改时间") @Transient @JsonIgnore get() = mrd

  /** 逻辑删除标志 */
  @JsonIgnore
  @Basic(fetch = FetchType.LAZY)
  @Deprecated(message = "不建议直接调用", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("dbEntityShadowRemoveTag"))
  @Column(name = LDF)
  @Schema(hidden = true, title = "逻辑删除标志")
  var ldf: Boolean? = null

  @Suppress("DEPRECATION_ERROR")
  @get:Schema(title = "是否已经删除")
  val dbEntityShadowRemoveTag: Boolean
    @Transient @JsonIgnore get() = ldf == true

  @Transient
  @JsonIgnore
  @Suppress("DEPRECATION_ERROR")
  override fun toNewEntity() {
    super.toNewEntity()
    ldf = false
    rlv = 0L
    crd = datetime.now()
    mrd = null
  }

  @Suppress("DEPRECATION_ERROR")
  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    ldf = false
    rlv = null
    crd = null
    mrd = null
    recordChangedSensitiveData()
  }
}

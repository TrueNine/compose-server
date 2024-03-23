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
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import jakarta.persistence.Version
import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.alias.Id
import net.yan100.compose.core.alias.bool
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
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
abstract class IEntity : AnyEntity() {
  companion object {
    const val RLV = DataBaseBasicFieldNames.LOCK_VERSION
    const val LDF = DataBaseBasicFieldNames.LOGIC_DELETE_FLAG
    const val CRD = DataBaseBasicFieldNames.CREATE_ROW_DATETIME
    const val MRD = DataBaseBasicFieldNames.MODIFY_ROW_DATETIME
  }

  /** 乐观锁版本 */
  @Version @JsonIgnore @Column(name = RLV) @Schema(hidden = true, title = "乐观锁版本", requiredMode = RequiredMode.NOT_REQUIRED) var rlv: BigSerial? = null
  val databaseTableRowFieldLockVersion: BigSerial?
    @Schema(title = "字段乐观锁版本号") @Transient @JsonIgnore get() = rlv

  @CreatedDate @JsonIgnore @Schema(title = "表行创建时间") @Column(name = CRD) var crd: datetime? = null
  val databaseTableRowFieldCreatedDatetime: datetime?
    @Schema(title = "字段创建时间") @Transient @JsonIgnore get() = crd

  @JsonIgnore @LastModifiedDate @Schema(title = "表行修改时间") @Column(name = MRD) var mrd: datetime? = null
  val databaseTableRowFieldLastModifyDatetime: datetime?
    @Schema(title = "字段的修改时间") @Transient @JsonIgnore get() = mrd

  /** 逻辑删除标志 */
  @JsonIgnore
  @Column(name = LDF)
  @Schema(hidden = true, title = "逻辑删除标志", requiredMode = RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
  var ldf: Boolean? = null
  val databaseTableRowFieldLogicDeleteFlag: bool
    @Schema(title = "是否已经删除") @Transient @JsonIgnore get() = ldf == true

  @Transient
  @JsonIgnore
  override fun asNew() {
    super.asNew()
    ldf = false
    rlv = null
  }

  override fun toString(): String {
    return withToString(super.toString(), LDF to ldf, RLV to rlv, CRD to crd, MRD to mrd)
  }
}

/**
 * ## 合并从数据库内查询的实体
 *
 * @param target 需合并对象
 * @param findByIdFn 查询函数
 * @param preMergeFn 合并前处理函数
 */
fun <T : IEntity> T.merge(
  target: T,
  findByIdFn: (id: Id) -> T?,
  preMergeFn: (dbData: T, thisData: T) -> T = { _, h -> h },
): T {
  return takeUpdate {
    val queryEntity = findByIdFn(target.id)
    checkNotNull(queryEntity) { "未找到修改的数据版本" }

    target.rlv = queryEntity.rlv
    target.mrd = datetime.now()

    preMergeFn(queryEntity, target)
  } ?: throw IllegalArgumentException("未找到修改的数据")
}

fun <T : IEntity> Iterable<T>.mergeAll(
  targets: List<T>,
  findAllByIdFn: (ids: List<Id>) -> List<T>,
  checkLength: Boolean = true,
  preMergeFn: (dbData: T, thisData: T) -> T = { _, h -> h },
): List<T> {
  val prepard = targets.filterNot { it.isNew }
  if (checkLength) check(targets.size == prepard.size) { "需更新的长度不一致" }

  val dbDatas = findAllByIdFn(prepard.map { it.id })
  if (checkLength) check(dbDatas.size == prepard.size) { "需更新的长度不一致" }

  val pd = dbDatas.associateBy { prepard.find { d -> it.id == d.id }!! }
  val allSave =
    pd.map {
      val p = it.key
      val d = it.value
      p.rlv = d.rlv
      p.mrd = datetime.now()
      preMergeFn(d, p)
    }
  if (checkLength) check(allSave.size == prepard.size) { "需更新的长度不一致" }
  return allSave
}

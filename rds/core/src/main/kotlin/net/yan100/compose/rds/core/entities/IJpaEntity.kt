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

import jakarta.persistence.Transient
import jakarta.persistence.Version
import net.yan100.compose.core.bool
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.datetime
import net.yan100.compose.core.i64
import net.yan100.compose.meta.annotations.MetaAutoManagement
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

/**
 * jpa顶级抽象类
 *
 * @author TrueNine
 * @since 2022-12-12
 */
interface IJpaEntity : IJpaPersistentEntity {

  /** 乐观锁版本 */
  @get:Version
  @get:MetaAutoManagement
  var rlv: i64?

  /**
   * 创建时间
   */
  @get:CreatedDate
  @set:CreatedDate
  @get:MetaAutoManagement
  var crd: datetime?

  /**
   * 修改时间
   */
  @get:LastModifiedDate
  @set:LastModifiedDate
  @get:MetaAutoManagement
  var mrd: datetime?

  /** 逻辑删除标志 */
  @get:MetaAutoManagement
  var ldf: bool?

  @Transient
  @Suppress("DEPRECATION_ERROR")
  override fun toNewEntity() {
    super.toNewEntity()
    ldf = null
    rlv = null
    crd = null
    mrd = null
  }

  @Suppress("DEPRECATION_ERROR")
  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    ldf = null
    rlv = null
    crd = null
    mrd = null
    recordChangedSensitiveData()
  }

  companion object {
    const val RLV = IDbNames.ROW_LOCK_VERSION
    const val LDF = IDbNames.LOGIC_DELETE_FLAG
    const val CRD = IDbNames.CREATE_ROW_DATETIME
    const val MRD = IDbNames.MODIFY_ROW_DATETIME
  }
}

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

import jakarta.persistence.*
import net.yan100.compose.core.Id
import net.yan100.compose.core.bool
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.domain.ISensitivity
import net.yan100.compose.core.getDefaultNullableId
import net.yan100.compose.core.isId
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import org.springframework.data.domain.Persistable
import java.io.Serializable

/**
 * ## JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@MappedSuperclass
/*@EntityListeners(
  BizCodeInsertListener::class,
  SnowflakeIdInsertListener::class,
)*/
@Access(AccessType.PROPERTY)
interface IJpaPersistentEntity :
  ISensitivity,
  Persistable<Id>,
  IExtensionDefineScope,
  IEnhanceEntity, Serializable {
  companion object {

    /** 主键 */
    const val ID = IDbNames.ID
  }

  @MetaSkipGeneration
  override val isChangedToSensitiveData: bool
    @Transient
    get() = super.isChangedToSensitiveData

  /** id */
  fun setId(id: Id)

  /** id */
  @jakarta.persistence.Id
  @Column(name = ID)
  override fun getId(): Id

  @Suppress("DEPRECATION_ERROR")
  fun toNewEntity() {
    id = getDefaultNullableId()
  }

  @Transient
  override fun isNew(): Boolean {
    return id.isId()
  }

  override fun recordChangedSensitiveData() {
  }

  @Suppress("DEPRECATION_ERROR")
  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    require(!isChangedToSensitiveData) { "数据已经脱敏，无需重复执行" }
    this.id = getDefaultNullableId()
    recordChangedSensitiveData()
  }
}

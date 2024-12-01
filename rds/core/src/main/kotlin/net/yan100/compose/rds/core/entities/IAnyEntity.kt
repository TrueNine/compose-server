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

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.Id
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.domain.ISensitivity
import net.yan100.compose.rds.core.listener.BizCodeInsertListener
import net.yan100.compose.rds.core.listener.SnowflakeIdInsertListener
import org.springframework.data.domain.Persistable
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
interface IAnyEntity : ISensitivity,
  Persistable<Id>,
  IExtensionDefineScope,
  IEnhanceEntity, Serializable {
  companion object {

    /** 主键 */
    const val ID = IDbNames.ID
  }

  /** id */
  fun setId(id: Id) {
    throw NotImplementedError("entity not implement primary id set function")
  }

  /** id */
  @jakarta.persistence.Id
  @Column(name = ID)
  override fun getId(): Id = throw NotImplementedError("entity not implement primary id get function")

  fun toNewEntity() {
    id = ""
  }


  override fun isNew(): Boolean {
    return "" == id || "null" == id
  }

  override fun recordChangedSensitiveData() {
  }

  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    require(!isChangedToSensitiveData) { "数据已经脱敏，无需重复执行" }
    this.id = ""
    recordChangedSensitiveData()
  }
}

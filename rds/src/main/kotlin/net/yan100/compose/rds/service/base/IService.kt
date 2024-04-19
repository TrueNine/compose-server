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
package net.yan100.compose.rds.service.base

import net.yan100.compose.core.alias.Id
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.Pw

/**
 * # 单一 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
@JvmDefaultWithoutCompatibility
interface IService<T : IEntity> {
  /**
   * ## 可重写的 保存
   *
   * @param e 实体
   */
  fun saveExists(e: T): T = save(e)

  fun findAll(page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun findAllOrderByIdDesc(page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun findAllOrderByIdDesc(): List<T>

  fun findAllByNotLogicDeleted(page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun findById(id: Id): T?

  fun findAllById(ids: List<Id>): MutableList<T>

  fun findByIdAndNotLogicDeleted(id: Id): T

  fun findByIdAndNotLogicDeletedOrNull(id: Id): T?

  fun findAllByIdAndNotLogicDeleted(ids: List<Id>, page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun countAll(): Long

  fun countAllByNotLogicDeleted(): Long

  fun existsById(id: Id): Boolean

  fun findLdfById(id: Id): Boolean

  fun save(e: T): T

  fun saveAll(es: List<T>): List<T>

  fun deleteById(id: Id)

  fun deleteAllById(ids: List<Id>)

  fun logicDeleteById(id: Id): T?

  fun logicDeleteAllById(ids: List<Id>): List<T>
}

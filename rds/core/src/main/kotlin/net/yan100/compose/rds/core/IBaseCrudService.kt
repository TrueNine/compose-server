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
package net.yan100.compose.rds.core

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.core.Id
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.repository.findByIdOrNull

/**
 * # 单一 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
interface IBaseCrudService<T : IEntity, R : IRepo<T>> {
  @get:JsonIgnore
  @get:Transient
  val repo: R

  /**
   * ## 可重写的 保存
   *
   * @param e 实体
   */
  fun postFound(e: T): T = post(e)

  /**
   * ## 可重写的 批量保存
   *
   * @param es 实体集合
   */
  fun postAllFound(es: List<T>): List<T> = postAll(es)

  fun fetchAll(page: Pq? = Pq.DEFAULT_MAX): Pr<T> = repo.findAll(page.page).result

  fun fetchAllOrderByIdDesc(page: Pq? = Pq.DEFAULT_MAX): Pr<T> = repo.findAllOrderByIdDesc(page.page).result

  fun fetchAllOrderByIdDesc(): List<T> = repo.findAllOrderByIdDesc()

  fun fetchAllByNotShadowRemoved(page: Pq? = Pq.DEFAULT_MAX): Pr<T> = repo.findAllByNotLogicDeleted(page.page).result

  fun fetchById(id: Id): T? = repo.findByIdOrNull(id)

  fun fetchAllById(ids: List<Id>): MutableList<T> = repo.findAllById(ids)

  fun fetchByIdAndNotShadowRemoved(id: Id): T = repo.findByIdAndNotLogicDelete(id)

  fun fetchByIdAndNotShadowRemovedOrNull(id: Id): T? = repo.findByIdAndNotLogicDeleteOrNull(id)

  fun fetchAllByIdAndNotShadowRemoved(ids: List<Id>, page: Pq? = Pq.DEFAULT_MAX): Pr<T> = repo.findAllByIdAndNotLogicDeleted(ids, page.page).result

  fun lenAll(): Long = repo.count()

  fun lenAllByNotShadowRemoved(): Long = repo.countByNotLogicDeleted()

  fun foundById(id: Id): Boolean = repo.existsById(id)

  fun foundShadowRemovedById(id: Id): Boolean = repo.findLdfById(id) ?: false

  fun post(e: T): T = repo.save(e)

  fun postAll(es: List<T>): List<T> = repo.saveAll(es)

  fun removeById(id: Id) = repo.deleteById(id)

  fun removeAllById(ids: List<Id>) = repo.deleteAllById(ids)

  fun shadowRemoveById(id: Id): T? = repo.logicDeleteById(id)

  fun shadowRemoveAllById(ids: List<Id>): List<T> = repo.logicDeleteAllById(ids)
}



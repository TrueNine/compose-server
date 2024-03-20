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
package net.yan100.compose.rds.repositories.base

import jakarta.validation.Valid
import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.alias.Id
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.page
import net.yan100.compose.rds.core.util.result
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@NoRepositoryBean
interface ILogicDeleteRepo<T : IEntity> : IAnyRepo<T> {
  @Query("from #{#entityName} e where e.id in :ids and (e.ldf = false or e.ldf is null)")
  fun findAllByIdAndNotLogicDeleted(ids: List<Id>, page: Pageable): Page<T>

  @Query(
    "select count(e.id) > 0 from #{#entityName} e where e.id = :id and (e.ldf = false or e.ldf is null)"
  )
  fun existsByIdAndNotLogicDeleted(id: Id)

  @Query("from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findByIdAndNotLogicDeleteOrNull(id: Id): T?

  @Query("from #{#entityName} e where e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: Id): T? =
    findByIdOrNull(id)?.let {
      it.ldf = true
      save(it)
    }

  @Query("select count(e.id) from #{#entityName} e where e.ldf = false or e.ldf is null")
  fun countByNotLogicDeleted(): BigSerial

  fun findByIdAndNotLogicDelete(id: Id): T = findByIdAndNotLogicDeleteOrNull(id)!!

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteAllById(ids: List<Id>): List<T> =
    findAllById(ids).filter { !it.ldf!! }.apply { saveAll(this) }

  fun findAllByNotLogicDeleted(@Valid pq: Pq?): Pr<T> = findAllByNotLogicDeleted(pq.page).result
}

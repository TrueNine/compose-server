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
import net.yan100.compose.core.alias.Pq
import net.yan100.compose.core.alias.Pr
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.util.page
import net.yan100.compose.rds.core.util.result
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IRepo<T : IEntity> : IAnyRepo<T>, ILogicDeleteRepo<T> {
  @Query("from #{#entityName} e order by e.id desc") fun findAllOrderByIdDesc(): List<T>

  @Query("from #{#entityName} e order by e.id desc") fun findAllOrderByIdDesc(page: Pageable): Page<T>

  @Query("select (e.ldf = false) from #{#entityName} e where e.id = :id") fun findLdfById(id: Id): Boolean?

  @Query("select e.rlv from #{#entityName} e where e.id = :id") fun findRlvById(id: Id): BigSerial

  fun findAll(@Valid pq: Pq?): Pr<T> = findAll(pq.page).result
}

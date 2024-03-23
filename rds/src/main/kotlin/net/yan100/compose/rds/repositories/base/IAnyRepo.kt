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

import net.yan100.compose.core.alias.Id
import net.yan100.compose.rds.core.entities.AnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.FluentQuery

/**
 * # 任意实体通用 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean interface IAnyRepo<T : AnyEntity> : JpaRepository<T, Id>, CrudRepository<T, Id>, QuerydslPredicateExecutor<T>, JpaSpecificationExecutor<T>

fun <E : IEntity, R> IAnyRepo<E>.findByQueryDsl(
  predicate: com.querydsl.core.types.Predicate,
  optFn: (q: FluentQuery.FetchableFluentQuery<E>) -> R,
): R {
  return findBy(predicate) { it: FluentQuery.FetchableFluentQuery<E> -> optFn(it) }
}

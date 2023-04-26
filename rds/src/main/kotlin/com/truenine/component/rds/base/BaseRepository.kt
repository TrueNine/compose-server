package com.truenine.component.rds.base

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional


@NoRepositoryBean
@JvmDefaultWithCompatibility
interface BaseRepository<T : BaseEntity> : AnyRepository<T> {
  @Query("from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findByIdAndNotLogicDelete(id: Long): T

  @Query("from #{#entityName} e where e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  @Query("from #{#entityName} e where e.id in :ids and e.ldf = false")
  fun findAllByIdAndNotLogicDeleted(ids: List<Long>, page: Pageable): Page<T>

  @Query("select e.ldf from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findLdfById(id: Long): Boolean?

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: Long): T? = findByIdOrNull(id)?.let { it.ldf = true;save(it) }

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteAllById(ids: List<Long>): List<T> = findAllById(ids).filter { !it.ldf }.apply { saveAll(this) }

  @Query("select count(e.id) from #{#entityName} e where e.ldf = false")
  fun countByNotLogicDeleted(): Long
}

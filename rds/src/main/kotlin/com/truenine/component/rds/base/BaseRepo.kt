package com.truenine.component.rds.base

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@NoRepositoryBean
interface BaseRepo<T : BaseEntity> : DbAnyRepo<T> {

  @Query(
    """
    from #{#entityName} e
    where e.id = :id
    and e.ldf = false
  """
  )
  fun findByIdNotLogicDelete(id: Long): T?

  @Query("from #{#entityName} e where e.id = :id and e.ldf = true")
  fun findLdfById(id: Long): Boolean

  @Transactional(rollbackFor = [Exception::class])
  fun logicDelete(t: T): T? = logicDeleteById(t.id)

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: Long): T? = findByIdOrNull(id)?.let { it.ldf = true;save(it) }
}

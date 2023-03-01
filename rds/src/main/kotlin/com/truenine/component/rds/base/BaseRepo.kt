package com.truenine.component.rds.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@NoRepositoryBean
interface BaseRepo<T : com.truenine.component.rds.base.BaseDao, ID : Serializable> :
  JpaRepository<T, ID>,
  CrudRepository<T, ID>,
  JpaSpecificationExecutor<T> {

  @Query(
    """
    from #{#entityName} e
    where e.id = :id
    and e.ldf = false
  """
  )
  fun findByIdNotLogicDelete(id: ID)

  @Suppress("UNCHECKED_CAST")
  @Transactional(rollbackFor = [Exception::class])
  fun logicDelete(t: T): Int {
    return logicDeleteById(t.id as ID)
  }

  @Suppress("UNCHECKED_CAST")
  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: ID?): Int {
    return if (id != null) {
      val entity = findByIdOrNull(id)
      if (null != entity) {
        entity.setLdf(true);save(entity);1
      } else 0
    } else 0
  }
}

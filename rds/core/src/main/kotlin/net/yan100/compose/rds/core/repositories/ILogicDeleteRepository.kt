package net.yan100.compose.rds.core.repositories

import net.yan100.compose.core.Id
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.i64
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.toPageable
import net.yan100.compose.rds.core.toPr
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull

@NoRepositoryBean
interface ILogicDeleteRepository<T : IJpaEntity> : IBaseRepository<T> {
  /** ## 查询逻辑删除标志 */
  @Query("select (e.ldf = false) from #{#entityName} e where e.id = :id")
  fun findLdfById(id: Id): Boolean?

  @Query(
    "from #{#entityName} e where e.id in :ids and (e.ldf = false or e.ldf is null)"
  )
  fun findAllByIdAndNotLogicDeleted(ids: List<Id>, page: Pageable): Page<T>

  @Query(
    "select count(e.id) > 0 from #{#entityName} e where e.id = :id and (e.ldf = false or e.ldf is null)"
  )
  fun existsByIdAndNotLogicDeleted(id: Id)

  @Modifying
  @Query("update #{#entityName} e set e.ldf = :ldf where e.id = :id")
  @Deprecated(
    message = "不建议直接调用",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("logicDeleteById"),
  )
  fun updateFlagById(id: Id, ldf: Boolean)

  @Modifying
  @Query("update #{#entityName} e set e.ldf = :ldf where e.id in :ids")
  @Deprecated(
    message = "不建议直接调用",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("logicDeleteAllById"),
  )
  fun updateAllFlagById(ids: List<Id>, ldf: Boolean)

  @ACID
  @Suppress("DEPRECATION_ERROR")
  fun logicDeleteById(id: Id): T? =
    findIdOrNullById(id)?.let {
      updateFlagById(it, true)
      findByIdOrNull(it)
    }

  @ACID
  @Suppress("DEPRECATION_ERROR")
  fun logicDeleteAllById(ids: List<Id>): List<T> {
    updateAllFlagById(findAllIdById(ids), true)
    return findAllById(ids)
  }

  @Query(
    "select count(e.id) from #{#entityName} e where e.ldf = false or e.ldf is null"
  )
  fun countByNotLogicDeleted(): i64

  @Query("from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findByIdAndNotLogicDeleteOrNull(id: Id): T?

  fun findByIdAndNotLogicDelete(id: Id): T =
    findByIdAndNotLogicDeleteOrNull(id)!!

  @Query("from #{#entityName} e where e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  fun findAllByNotLogicDeleted(pq: Pq?): Pr<T> =
    findAllByNotLogicDeleted(pq.toPageable()).toPr()
}

package net.yan100.compose.rds.repository.base

import net.yan100.compose.rds.core.entity.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@JvmDefaultWithCompatibility
@NoRepositoryBean
interface IRepo<T : BaseEntity> : IAnyRepo<T> {
  fun findByIdAndNotLogicDelete(id: String): T = findByIdAndNotLogicDeleteOrNull(id)!!

  @Query("from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findByIdAndNotLogicDeleteOrNull(id: String): T?

  @Query("from #{#entityName} e where e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  @Query("from #{#entityName} e where e.id in :ids and (e.ldf = false or e.ldf is null)")
  fun findAllByIdAndNotLogicDeleted(ids: List<String>, page: Pageable): Page<T>

  @Query("select (e.ldf = false) from #{#entityName} e where e.id = :id")
  fun findLdfById(id: String): Boolean?

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: String): T? = findByIdOrNull(id)?.let { it.ldf = true;save(it) }

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteAllById(ids: List<String>): List<T> = findAllById(ids).filter { !it.ldf!! }.apply { saveAll(this) }

  @Query("select count(e.id) from #{#entityName} e where e.ldf = false or e.ldf is null")
  fun countByNotLogicDeleted(): Long

  @Query("select count(e.id) > 0 from #{#entityName} e where e.id = :id and (e.ldf = false or e.ldf is null)")
  fun existsByIdAndNotLogicDeleted(id: String)

  @Query("select e.rlv from #{#entityName} e where e.id = :id")
  fun findRlvById(id: String): Long

  fun modifyWrapper(e: T): T {
    return if (e.id != null) e.also { it.rlv = findRlvById(e.id!!) }
    else e
  }
}

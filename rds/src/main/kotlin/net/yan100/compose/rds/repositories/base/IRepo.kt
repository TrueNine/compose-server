package net.yan100.compose.rds.repositories.base

import jakarta.validation.Valid
import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.alias.Id
import net.yan100.compose.rds.core.entities.BaseEntity
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

@JvmDefaultWithCompatibility
@NoRepositoryBean
interface IRepo<T : BaseEntity> : IAnyRepo<T> {
  fun findByIdAndNotLogicDelete(id: Id): T = findByIdAndNotLogicDeleteOrNull(id)!!

  @Query("from #{#entityName} e order by e.id desc")
  fun findAllOrderByIdDesc(): List<T>

  @Query("from #{#entityName} e order by e.id desc")
  fun findAllOrderByIdDesc(page: Pageable): Page<T>

  @Query("from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findByIdAndNotLogicDeleteOrNull(id: Id): T?

  @Query("from #{#entityName} e where e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  @Query("from #{#entityName} e where e.id in :ids and (e.ldf = false or e.ldf is null)")
  fun findAllByIdAndNotLogicDeleted(ids: List<Id>, page: Pageable): Page<T>

  @Query("select (e.ldf = false) from #{#entityName} e where e.id = :id")
  fun findLdfById(id: Id): Boolean?

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: Id): T? = findByIdOrNull(id)?.let { it.ldf = true;save(it) }

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteAllById(ids: List<Id>): List<T> = findAllById(ids).filter { !it.ldf!! }.apply { saveAll(this) }

  @Query("select count(e.id) from #{#entityName} e where e.ldf = false or e.ldf is null")
  fun countByNotLogicDeleted(): BigSerial

  @Query("select count(e.id) > 0 from #{#entityName} e where e.id = :id and (e.ldf = false or e.ldf is null)")
  fun existsByIdAndNotLogicDeleted(id: Id)

  @Query("select e.rlv from #{#entityName} e where e.id = :id")
  fun findRlvById(id: Id): BigSerial


  fun findAll(@Valid pq: Pq?): Pr<T> = findAll(pq.page).result
  fun findAllByNotLogicDeleted(@Valid pq: Pq?): Pr<T> = findAllByNotLogicDeleted(pq.page).result
}

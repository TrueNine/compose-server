package net.yan100.compose.rds.service.base


import jakarta.validation.Valid
import net.yan100.compose.core.alias.Id
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.page
import net.yan100.compose.rds.core.util.result
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.repository.findByIdOrNull

abstract class CrudService<T : IEntity>(private val repo: IRepo<T>) : IService<T> {
  override fun findAllByIdAndNotLogicDeleted(ids: List<Id>, page: Pq?): Pr<T> = repo.findAllByIdAndNotLogicDeleted(ids, page.page).result
  override fun findAllByNotLogicDeleted(@Valid page: Pq?): Pr<T> = repo.findAllByNotLogicDeleted(page.page).result

  override fun findAll(@Valid page: Pq?): Pr<T> = repo.findAll(page.page).result
  override fun findAllOrderByIdDesc(page: Pq?): Pr<T> = repo.findAllOrderByIdDesc(page.page).result

  override fun findAllOrderByIdDesc(): List<T> = repo.findAllOrderByIdDesc()

  override fun findById(id: Id): T? = repo.findByIdOrNull(id)
  override fun findAllById(ids: List<Id>): MutableList<T> = repo.findAllById(ids)
  override fun findByIdAndNotLogicDeleted(id: Id): T = repo.findByIdAndNotLogicDelete(id)
  override fun findByIdAndNotLogicDeletedOrNull(id: Id): T? = repo.findByIdAndNotLogicDeleteOrNull(id)

  override fun findLdfById(id: Id): Boolean = repo.findLdfById(id) ?: false
  override fun countAll(): Long = repo.count()
  override fun countAllByNotLogicDeleted(): Long = repo.countByNotLogicDeleted()
  override fun existsById(id: Id): Boolean = repo.existsById(id)

  override fun save(e: T): T = repo.save(e)
  override fun saveAll(es: List<T>): List<T> = repo.saveAll(es)

  override fun deleteById(id: Id) = repo.deleteById(id)
  override fun deleteAllById(ids: List<Id>) = repo.deleteAllById(ids)
  override fun logicDeleteById(id: Id): T? = repo.logicDeleteById(id)

  override fun logicDeleteAllById(ids: List<Id>): List<T> = repo.logicDeleteAllById(ids)
}

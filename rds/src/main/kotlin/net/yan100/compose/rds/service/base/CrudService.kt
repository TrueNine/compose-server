package net.yan100.compose.rds.service.base

import jakarta.validation.Valid
import net.yan100.compose.rds.core.entity.BaseEntity
import net.yan100.compose.rds.repository.base.IRepo
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.page
import net.yan100.compose.rds.core.util.result
import org.springframework.data.repository.findByIdOrNull

abstract class CrudService<T : BaseEntity>(private val repo: IRepo<T>) : IService<T> {
  override fun findAllByIdAndNotLogicDeleted(ids: List<String>, page: Pq?): Pr<T> {
    return repo.findAllByIdAndNotLogicDeleted(ids, page.page).result
  }

  override fun findAllByNotLogicDeleted(@Valid page: Pq?): Pr<T> {
    return repo.findAllByNotLogicDeleted(page.page).result
  }

  override fun findAll(@Valid page: Pq?): Pr<T> = repo.findAll(page.page).result
  override fun findById(id: String): T? = repo.findByIdOrNull(id)
  override fun findAllById(ids: List<String>): MutableList<T> = repo.findAllById(ids)
  override fun findByIdAndNotLogicDeleted(id: String): T = repo.findByIdAndNotLogicDelete(id)
  override fun findByIdAndNotLogicDeletedOrNull(id: String): T? = repo.findByIdAndNotLogicDeleteOrNull(id)

  override fun findLdfById(id: String): Boolean = repo.findLdfById(id) ?: false
  override fun countAll(): Long = repo.count()
  override fun countAllByNotLogicDeleted(): Long = repo.countByNotLogicDeleted()
  override fun existsById(id: String): Boolean = repo.existsById(id)

  override fun save(e: T): T = repo.save(e)
  override fun saveAll(es: List<T>): List<T> = repo.saveAll(es)

  override fun deleteById(id: String) = repo.deleteById(id)
  override fun deleteAllById(ids: List<String>) = repo.deleteAllById(ids)
  override fun logicDeleteById(id: String): T? = repo.logicDeleteById(id)

  override fun logicDeleteAllById(ids: List<String>): List<T> = repo.logicDeleteAllById(ids)
}

package com.truenine.component.rds.base

import com.truenine.component.rds.util.Pq
import com.truenine.component.rds.util.Pr
import com.truenine.component.rds.util.page
import com.truenine.component.rds.util.result
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull


abstract class BaseServiceImpl<T : BaseEntity>(
  private val repo: BaseRepository<T>
) : BaseService<T> {
  override fun findAllByIdAndNotLogicDeleted(ids: List<String>, page: Pq?): Pr<T> =
    repo.findAllByIdAndNotLogicDeleted(ids, page.page).result

  override fun findAllByNotLogicDeleted(@Valid page: Pq?): Pr<T> =
    repo.findAllByNotLogicDeleted(page.page).result

  override fun findAll(@Valid page: Pq?): Pr<T> = repo.findAll(page.page).result
  override fun findById(id: String): T? = repo.findByIdOrNull(id)
  override fun findAllById(ids: List<String>): MutableList<T> = repo.findAllById(ids)
  override fun findByIdAndNotLogicDeleted(id: String): T = repo.findByIdAndNotLogicDelete(id)
  override fun findByIdAndNotLogicDeletedOrNull(id: String): T? = repo.findByIdAndNotLogicDeleteOrNull(id)

  override fun findLdfById(id: String): Boolean = repo.findLdfById(id) ?: true
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

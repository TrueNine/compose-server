package com.truenine.component.rds.base

import com.truenine.component.rds.util.page
import com.truenine.component.rds.util.result
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull


abstract class BaseServiceImpl<T : BaseEntity>(
  private val repo: BaseRepository<T>
) : BaseService<T> {
  override fun findAllByIdAndNotLogicDeleted(ids: List<Long>, page: PagedRequestParam?): PagedResponseResult<T> =
    repo.findAllByIdAndNotLogicDeleted(ids, page.page).result

  override fun findAllByNotLogicDeleted(@Valid page: PagedRequestParam?): PagedResponseResult<T> =
    repo.findAllByNotLogicDeleted(page.page).result

  override fun findAll(@Valid page: PagedRequestParam?): PagedResponseResult<T> = repo.findAll(page.page).result
  override fun findById(id: Long): T? = repo.findByIdOrNull(id)
  override fun findAllById(ids: List<Long>): MutableList<T> = repo.findAllById(ids)
  override fun findByIdAndNotLogicDeleted(id: Long): T = repo.findByIdAndNotLogicDelete(id)
  override fun findByIdAndNotLogicDeletedOrNull(id: Long): T? = repo.findByIdAndNotLogicDeleteOrNull(id)

  override fun findLdfById(id: Long): Boolean = repo.findLdfById(id) ?: true
  override fun countAll(): Long = repo.count()
  override fun countAllByNotLogicDeleted(): Long = repo.countByNotLogicDeleted()
  override fun existsById(id: Long): Boolean = repo.existsById(id)

  override fun save(e: T): T = repo.save(e)
  override fun saveAll(es: List<T>): List<T> = repo.saveAll(es)

  override fun deleteById(id: Long) = repo.deleteById(id)
  override fun deleteAllById(ids: List<Long>) = repo.deleteAllById(ids)
  override fun logicDeleteById(id: Long): T? = repo.logicDeleteById(id)

  override fun logicDeleteAllById(ids: List<Long>): List<T> = repo.logicDeleteAllById(ids)
}

package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper
import org.springframework.data.repository.findByIdOrNull

abstract class BaseServiceImpl<T : BaseEntity>(
  private val repo: BaseRepo<T>
) : BaseService<T> {

  override fun findAll(page: PagedRequestParam?): PagedResponseResult<T> = repo.findAll(PagedWrapper.param(page)).run { PagedWrapper.result(this) }
  override fun findById(id: Long): T? = repo.findByIdOrNull(id)
  override fun findAllById(ids: List<Long>): MutableList<T> = repo.findAllById(ids)
  override fun findByIdNotLogicDelete(id: Long): T? = repo.findByIdNotLogicDelete(id)

  override fun findLdfById(id: Long): Boolean? = repo.findLdfById(id)
  override fun countAll(): Long = repo.count()
  override fun existsById(id: Long): Boolean = repo.existsById(id)

  override fun save(e: T): T? = repo.save(e)
  override fun saveAll(es: List<T>): List<T> = repo.saveAll(es)

  override fun deleteById(id: Long) = repo.deleteById(id)
  override fun deleteAllById(ids: List<Long>) = repo.deleteAllById(ids)
  override fun logicDeleteById(id: Long): T? = repo.logicDeleteById(id)
}

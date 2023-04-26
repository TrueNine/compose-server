package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper

interface BaseService<T : AnyEntity> {
  fun findAll(page: PagedRequestParam? = PagedWrapper.DEFAULT_MAX): PagedResponseResult<T>
  fun findAllByNotLogicDeleted(page: PagedRequestParam? = PagedWrapper.DEFAULT_MAX): PagedResponseResult<T>

  fun findById(id: Long): T?
  fun findAllById(ids: List<Long>): MutableList<T>
  fun findByIdAndNotLogicDelete(id: Long): T
  fun findAllByIdAndNotLogicDeleted(ids: List<Long>, page: PagedRequestParam? = PagedWrapper.DEFAULT_MAX): PagedResponseResult<T>

  fun countAll(): Long
  fun countAllByNotLogicDeleted(): Long
  fun existsById(id: Long): Boolean

  fun findLdfById(id: Long): Boolean

  fun save(e: T): T
  fun saveAll(es: List<T>): List<T>

  fun deleteById(id: Long)
  fun deleteAllById(ids: List<Long>)

  fun logicDeleteById(id: Long): T?
  fun logicDeleteAllById(ids: List<Long>): List<T>
}

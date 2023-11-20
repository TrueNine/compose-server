package net.yan100.compose.rds.service.base

import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.Pw

/**
 * # 单一 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
interface IService<T : BaseEntity> {
  fun findAll(page: Pq? = Pw.DEFAULT_MAX): Pr<T>
  fun findAllOrderByIdDesc(page: Pq? = Pw.DEFAULT_MAX): Pr<T>
  fun findAllOrderByIdDesc(): List<T>
  fun findAllByNotLogicDeleted(page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun findById(id: String): T?
  fun findAllById(ids: List<String>): MutableList<T>
  fun findByIdAndNotLogicDeleted(id: String): T
  fun findByIdAndNotLogicDeletedOrNull(id: String): T?

  fun findAllByIdAndNotLogicDeleted(ids: List<String>, page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun countAll(): Long
  fun countAllByNotLogicDeleted(): Long
  fun existsById(id: String): Boolean

  fun findLdfById(id: String): Boolean

  fun save(e: T): T
  fun saveAll(es: List<T>): List<T>

  fun deleteById(id: String)
  fun deleteAllById(ids: List<String>)

  fun logicDeleteById(id: String): T?
  fun logicDeleteAllById(ids: List<String>): List<T>
}

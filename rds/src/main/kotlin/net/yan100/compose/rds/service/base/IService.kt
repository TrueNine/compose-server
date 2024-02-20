package net.yan100.compose.rds.service.base

import net.yan100.compose.core.alias.Id
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.Pw

/**
 * # 单一 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
@JvmDefaultWithoutCompatibility
interface IService<T : IEntity> {
    fun findAll(page: Pq? = Pw.DEFAULT_MAX): Pr<T>
    fun findAllOrderByIdDesc(page: Pq? = Pw.DEFAULT_MAX): Pr<T>
    fun findAllOrderByIdDesc(): List<T>
    fun findAllByNotLogicDeleted(page: Pq? = Pw.DEFAULT_MAX): Pr<T>

    fun findById(id: Id): T?
    fun findAllById(ids: List<Id>): MutableList<T>
    fun findByIdAndNotLogicDeleted(id: Id): T
    fun findByIdAndNotLogicDeletedOrNull(id: Id): T?

    fun findAllByIdAndNotLogicDeleted(ids: List<Id>, page: Pq? = Pw.DEFAULT_MAX): Pr<T>

    fun countAll(): Long
    fun countAllByNotLogicDeleted(): Long
    fun existsById(id: Id): Boolean

    fun findLdfById(id: Id): Boolean

    fun save(e: T): T
    fun saveAll(es: List<T>): List<T>

    fun deleteById(id: Id)
    fun deleteAllById(ids: List<Id>)

    fun logicDeleteById(id: Id): T?
    fun logicDeleteAllById(ids: List<Id>): List<T>
}

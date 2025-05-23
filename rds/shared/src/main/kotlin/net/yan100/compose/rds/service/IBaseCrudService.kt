package net.yan100.compose.rds.service

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.Id
import net.yan100.compose.Pq
import net.yan100.compose.Pr
import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.annotations.ACID
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.entities.fromDbData
import net.yan100.compose.rds.toPageable
import net.yan100.compose.rds.toPr
import org.springframework.data.repository.findByIdOrNull

/**
 * # 单一 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
interface IBaseCrudService<T : IJpaEntity, R : IRepo<T>> {
  @get:JsonIgnore
  @get:Transient
  val repo: R

  /**
   * ## 可重写的 保存
   *
   * @param e 实体
   */
  @ACID
  fun postFound(e: T): T = post(e)

  /**
   * ## 可重写的 批量保存
   *
   * @param es 实体集合
   */
  @ACID
  fun postAllFound(es: List<T>): List<T> = postAll(es)

  fun fetchAllOrderByIdDesc(page: Pq? = Pq.DEFAULT_MAX): Pr<T> =
    repo.findAllOrderByIdDesc(page.toPageable()).toPr()

  fun fetchAll(pq: Pq? = Pq.DEFAULT_MAX): Pr<T> = fetchAllOrderByIdDesc(pq)

  fun fetchAllOrderByIdDesc(): List<T> = repo.findAllOrderByIdDesc()

  fun fetchAllByNotShadowRemoved(page: Pq? = Pq.DEFAULT_MAX): Pr<T> =
    repo.findAllByNotLogicDeleted(page.toPageable()).toPr()

  fun fetchById(id: Id): T? = repo.findByIdOrNull(id)

  fun fetchAllById(ids: List<Id>): MutableList<T> = repo.findAllById(ids)

  fun fetchByIdAndNotShadowRemoved(id: Id): T =
    repo.findByIdAndNotLogicDelete(id)

  fun fetchByIdAndNotShadowRemovedOrNull(id: Id): T? =
    repo.findByIdAndNotLogicDeleteOrNull(id)

  fun fetchAllByIdAndNotShadowRemoved(
    ids: List<Id>,
    page: Pq? = Pq.DEFAULT_MAX,
  ): Pr<T> = repo.findAllByIdAndNotLogicDeleted(ids, page.toPageable()).toPr()

  fun lenAll(): Long = repo.count()

  fun lenAllByNotShadowRemoved(): Long = repo.countByNotLogicDeleted()

  fun foundById(id: Id): Boolean = repo.existsById(id)

  fun foundShadowRemovedById(id: Id): Boolean = repo.findLdfById(id) == true

  @ACID
  fun post(e: T): T {
    return if (e.isNew) repo.save(e)
    else {
      val dbData = fetchById(id = e.id)
      checkNotNull(dbData) { "数据库中不存在该数据" }
      val mergedDbData = e.fromDbData(dbData)
      repo.save(mergedDbData)
    }
  }

  @ACID
  fun postAll(es: List<T>): List<T> = repo.saveAll(es)

  fun removeById(id: Id) = repo.deleteById(id)

  fun removeAllById(ids: List<Id>) = repo.deleteAllById(ids)

  fun shadowRemoveById(id: Id): T? = repo.logicDeleteById(id)

  fun shadowRemoveAllById(ids: List<Id>): List<T> = repo.logicDeleteAllById(ids)
}

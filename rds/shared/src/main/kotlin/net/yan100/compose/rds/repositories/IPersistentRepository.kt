package net.yan100.compose.rds.repositories

import net.yan100.compose.Id
import net.yan100.compose.rds.entities.IJpaPersistentEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

/**
 * # 任意实体通用 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean
interface IPersistentRepository<T : IJpaPersistentEntity> {
  /**
   * ## 根据 ID 查询 ID
   *
   * 这个查询存在的意义在于，当想获取一个存在的 id 时，可 以一次查询完成，而不必进行其他操作
   *
   * @param id 主键 id
   */
  @Query("select e.id from #{#entityName} e where e.id = :id")
  fun findIdOrNullById(id: Id): Id?

  /**
   * ## 根据 ID 批量查询 ID
   *
   * 这个查询存在的意义在于，当想获取一个存在的 id 时，可 以一次查询完成，而不必进行其他操作
   *
   * @param ids 主键 id
   */
  @Query("select e.id from #{#entityName} e where e.id in :ids")
  fun findAllIdById(ids: List<Id>): List<Id>
}

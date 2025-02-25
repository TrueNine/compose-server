package net.yan100.compose.rds.core

import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.repositories.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

private const val findAllOrderByIdDesc =
  "from #{#entityName} e order by e.id desc, e.mrd desc"

@NoRepositoryBean
interface IRepo<T : IJpaEntity> :
  IPersistentRepository<T>,
  IAuditRepository<T>,
  ILogicDeleteRepository<T>,
  IBaseRepository<T>,
  IQuerydslExtensionRepository<T> {
  @Query("from #{#entityName} e order by e.id desc, e.mrd desc")
  fun findAllOrderByIdDesc(): List<T>

  @Query(findAllOrderByIdDesc) fun findAllOrderByIdDesc(page: Pageable): Page<T>

  /**
   * ## 重写的 findAll 方法
   * - 使用 jpql 控制
   * - 根据 id 进行倒排
   * - 根据 mrd 进行倒排
   */
  override fun findAll(page: Pageable): Page<T> {
    return findAllOrderByIdDesc(page)
  }

  /*@Query("""
    select new net.yan100.compose.rds.core.domain.PersistenceAuditData(
      e.ldf,
      e.rlv,
      e.id,
      e.crd,
      e.mrd
    )
    from #{#entityName} e
    where e.id = :id
  """)
  fun findPersistenceAuditDataById(id: Id): PersistenceAuditData?*/

}

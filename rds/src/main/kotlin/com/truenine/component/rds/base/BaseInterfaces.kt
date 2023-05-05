package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper
import org.apache.poi.ss.formula.functions.T
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

/**
 * # 任意实体通用 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean
interface AnyRepository<T : AnyEntity> :
  JpaRepository<T, Long>,
  CrudRepository<T, Long>,
  JpaSpecificationExecutor<T> {
}

@NoRepositoryBean
@JvmDefaultWithCompatibility
interface BaseRepository<T : BaseEntity> : AnyRepository<T> {

  fun findByIdAndNotLogicDelete(id: Long): T = findByIdAndNotLogicDeleteOrNull(id)!!

  @Query("from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findByIdAndNotLogicDeleteOrNull(id: Long): T?

  @Query("from #{#entityName} e where e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  @Query("from #{#entityName} e where e.id in :ids and e.ldf = false")
  fun findAllByIdAndNotLogicDeleted(ids: List<Long>, page: Pageable): Page<T>

  @Query("select e.ldf from #{#entityName} e where e.id = :id and e.ldf = false")
  fun findLdfById(id: Long): Boolean?

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: Long): T? = findByIdOrNull(id)?.let { it.ldf = true;save(it) }

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteAllById(ids: List<Long>): List<T> = findAllById(ids).filter { !it.ldf }.apply { saveAll(this) }

  @Query("select count(e.id) from #{#entityName} e where e.ldf = false")
  fun countByNotLogicDeleted(): Long
}

/**
 * # 线索树 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean
@JvmDefaultWithCompatibility
interface TreeRepository<T : TreeEntity> : BaseRepository<T> {

  fun findChildrenCount(parent: T): Long {
    require(parent.rln == null || parent.rrn == null) {
      "父节点：$parent 节点值为 null"
    }
    return (parent.rrn - parent.rln - 1) / 2
  }

  @Query(
    """
    from #{#entityName} e
    where e.rln between :#{#parent.rln} and :#{#parent.rrn}
  """
  )
  fun findChildren(parent: T): List<T>


  @Query(
    """
    from #{#entityName} e
    where e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
  )
  fun findParentPath(child: T): List<T>


  @Query(
    """
    select count(1) + 1
    from #{#entityName} e
    where e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
  )
  fun findTreeLevel(child: T): Long

  @Query(
    """
    update #{#entityName} e
    set e.rln = e.rln + :rlnOffset
    where e.rln > :parentRln
  """
  )
  @Modifying
  fun pushRlnByOffset(rlnOffset: Long, parentRln: Long)

  @Query(
    """
    update #{#entityName} e
    set e.rrn = e.rrn + :rrnOffset
    where e.rrn >= :parentRln
  """
  )
  @Modifying
  fun pushRrnByOffset(rrnOffset: Long, parentRln: Long)

  @Query(
    """
    update #{#entityName} c
    set c.rln = c.rln - :rlnOffset
    where c.rln > :parentRln
  """
  )
  @Modifying
  fun popRlnByOffset(rlnOffset: Long, parentRln: Long)


  @Query(
    """
    update #{#entityName} c
    set c.rrn = c.rrn - :rrnOffset
    where c.rrn >= :parentRln
  """
  )
  @Modifying
  fun popRrnByOffset(rrnOffset: Long, parentRln: Long): Int

  /**
   * 保存一组 树结构，此操作为原子性的，一次事务
   * 只能进行一次操作
   *
   * @param parent 父节点
   * @param children 直属子节点列表
   * @return 保存后的子节点
   */
  @Transactional(rollbackFor = [Exception::class])
  fun saveChildren(
    parent: T,
    children: List<T>
  ): List<T> {
    if (children.isEmpty()) return listOf()
    require(
      parent.rln != null
        && parent.rrn != null
        && parent.id != null
    ) { "父节点缺少必要的值 = $parent" }
    val leftStep = parent.rln + 1
    val offset = (children.size * 2)
    // 更新所有的左节点和右节点
    pushRlnByOffset(offset.toLong(), parent.rln)
    pushRrnByOffset(offset.toLong(), parent.rln)
    // 编排并列的子节点
    for (i in 0 until (offset) step 2) {
      val idx = (i / 2)
      children[idx].rpi = parent.id
      children[idx].rln = leftStep + i
      children[idx].rrn = leftStep + i + 1
    }
    return saveAll(children)
  }

  @Transactional(rollbackFor = [Exception::class])
  fun saveChildren(
    parent: T,
    children: () -> List<T>
  ): List<T> = saveChildren(parent, children())


  @Transactional(rollbackFor = [Exception::class])
  fun saveChild(
    parent: T?,
    child: T
  ): T {
    return if (parent == null) {
      child.rln = 1
      child.rrn = 2
      child.rpi = null
      save(child)
    } else {
      pushRlnByOffset(2, parent.rln)
      pushRrnByOffset(2, parent.rln)
      child.rpi = parent.id
      child.rln = parent.rln + 1
      child.rrn = child.rln + 1
      save(child)
    }
  }


  @Transactional(rollbackFor = [Exception::class])
  fun deleteChild(child: T) {
    delete(child)
    popRlnByOffset(2, child.rln)
    popRrnByOffset(2, child.rln)
  }
}


/**
 * # 单一 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
interface BaseService<T : AnyEntity> {
  fun findAll(page: PagedRequestParam? = PagedWrapper.DEFAULT_MAX): PagedResponseResult<T>
  fun findAllByNotLogicDeleted(page: PagedRequestParam? = PagedWrapper.DEFAULT_MAX): PagedResponseResult<T>

  fun findById(id: Long): T?
  fun findAllById(ids: List<Long>): MutableList<T>
  fun findByIdAndNotLogicDeleted(id: Long): T
  fun findByIdAndNotLogicDeletedOrNull(id: Long): T?

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



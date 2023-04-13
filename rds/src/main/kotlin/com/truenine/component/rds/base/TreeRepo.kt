package com.truenine.component.rds.base

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Transactional

@NoRepositoryBean
@JvmDefaultWithCompatibility
interface TreeRepo<T : PresortTreeEntity> : BaseRepo<T> {

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

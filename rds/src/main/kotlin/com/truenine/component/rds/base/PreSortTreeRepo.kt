package com.truenine.component.rds.base

import com.truenine.component.core.dev.BetaTest
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@NoRepositoryBean
@JvmDefaultWithCompatibility
interface PreSortTreeRepo<T : PreSortTreeDao, ID : Serializable> :
  BaseRepo<T, ID> {

  fun findTreeMetaChildCountById(id: ID): Long {
    val entity = findById(id).orElseGet { null }
    return if (null != entity) {
      (entity.rrn - entity.rln - 1) / 2
    } else 0
  }

  @Query(
    """
    from #{#entityName} e
    where e.rln between :#{#parent.rln} and :#{#parent.rrn}
  """
  )
  fun findChild(parent: T): List<T>


  @Query(
    """
    from #{#entityName} e
    where e.rln < :#{#t.rln}
    and e.rrn > :#{#t.rrn}
  """
  )
  fun findParentPath(t: T): List<T>


  @Query(
    """
    select count(1) + 1
    from #{#entityName} e
    where e.rln < :#{#t.rln}
    and e.rrn > :#{#t.rrn}
  """
  )
  fun findTreeMetaLevel(t: T)

  @Query(
    """
    update #{#entityName} e
    set e.rln = e.rln + :rlnOffset
    where e.rln > :parentRln
  """
  )
  @Modifying
  fun updateAllPreCln(rlnOffset: Long, parentRln: Long)

  @Query(
    """
    update #{#entityName} e
    set e.rrn = e.rrn + :rrnOffset
    where e.rrn >= :parentRln
  """
  )
  @Modifying
  fun updateAllPreCrn(rrnOffset: Long, parentRln: Long)

  @Query(
    """
    update #{#entityName} c
    set c.rln = c.rln - :rlnOffset
    where c.rln > :parentRln
  """
  )
  @Modifying
  fun deleteAllPreRln(rlnOffset: Long, parentRln: Long)


  @Query(
    """
    update #{#entityName} c
    set c.rrn = c.rrn - :rrnOffset
    where c.rrn >= :parentRln
  """
  )
  @Modifying
  fun deleteAllPreCrn(rrnOffset: Long, parentRln: Long): Int


  @Transactional(
    rollbackFor = [Exception::class],
    propagation = Propagation.REQUIRES_NEW
  )
  fun saveAllChildrenByParentId(
    parent: T,
    children: List<T>
  ): List<T> {
    if (children.isEmpty()) return listOf()
    require(
      parent.rln != null
        && parent.rrn != null
        && parent.id != null
    )
    { "父节点没有必要的值 = $parent" }
    val leftStep = parent.rln + 1
    val offset = (children.size * 2)
    updateAllPreCln(offset.toLong(), parent.rln)
    updateAllPreCrn(offset.toLong(), parent.rln)
    for (i in 0 until (offset) step 2) {
      val idx = (i / 2)
      children[idx].rpi = parent.id
      children[idx].rln = leftStep + i
      children[idx].rrn = leftStep + i + 1
    }
    return saveAll(children)
  }

  @Transactional(
    rollbackFor = [Exception::class],
    propagation = Propagation.REQUIRES_NEW
  )
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
      updateAllPreCln(2, parent.rln)
      updateAllPreCrn(2, parent.rln)
      child.rpi = parent.id
      child.rln = parent.rln + 1
      child.rrn = child.rln + 1
      save(child)
    }
  }

  @Transactional(
    rollbackFor = [Exception::class],
    propagation = Propagation.REQUIRES_NEW
  )
  @BetaTest
  // TODO 完善此方法
  fun deleteChild(child: T) = run {
    child.takeIf {
      (it.rrn != null
        && it.rln != null)
    }?.run {
      delete(this)
      deleteAllPreRln(2, rln)
      deleteAllPreCrn(2, rln)
    }
  }
}

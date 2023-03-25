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
      (entity.crn - entity.cln - 1) / 2
    } else 0
  }

  @Query(
    """
    from #{#entityName} e
    where e.cln between :#{#parent.cln} and :#{#parent.crn}
  """
  )
  fun findChild(parent: T): List<T>


  @Query(
    """
    from #{#entityName} e
    where e.cln < :#{#t.cln}
    and e.crn > :#{#t.crn}
  """
  )
  fun findParentPath(t: T): List<T>


  @Query(
    """
    select count(1) + 1
    from #{#entityName} e
    where e.cln < :#{#t.cln}
    and e.crn > :#{#t.crn}
  """
  )
  fun findTreeMetaLevel(t: T)

  @Query(
    """
    update #{#entityName} e
    set e.cln = e.cln + :clnOffset
    where e.cln > :parentCln
  """
  )
  @Modifying
  fun updateAllPreCln(clnOffset: Long, parentCln: Long)

  @Query(
    """
    update #{#entityName} e
    set e.crn = e.crn + :crnOffset
    where e.crn >= :parentCln
  """
  )
  @Modifying
  fun updateAllPreCrn(crnOffset: Long, parentCln: Long)

  @Query(
    """
    update #{#entityName} c
    set c.cln = c.cln - :clnOffset
    where c.cln > :parentCln
  """
  )
  @Modifying
  fun deleteAllPreCln(clnOffset: Long, parentCln: Long)


  @Query(
    """
    update #{#entityName} c
    set c.crn = c.crn - :crnOffset
    where c.crn >= :parentCln
  """
  )
  @Modifying
  fun deleteAllPreCrn(crnOffset: Long, parentCln: Long): Int


  @Suppress("UNCHECKED_CAST")
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
      parent.cln != null
        && parent.crn != null
        && parent.id != null
    )
    { "父节点没有必要的值 = $parent" }
    val leftStep = parent.cln + 1
    val offset = (children.size * 2)
    updateAllPreCln(offset.toLong(), parent.cln)
    updateAllPreCrn(offset.toLong(), parent.cln)
    for (i in 0 until (offset) step 2) {
      val idx = (i / 2)
      children[idx].cpi = parent.id
      children[idx].cln = leftStep + i
      children[idx].crn = leftStep + i + 1
    }
    return saveAll(children)
  }

  @Suppress("UNCHECKED_CAST")
  @Transactional(
    rollbackFor = [Exception::class],
    propagation = Propagation.REQUIRES_NEW
  )
  fun saveChild(
    parent: T?,
    child: T
  ): T {
    return if (parent == null) {
      child.cln = 1
      child.crn = 2
      child.cpi = null
      save(child)
    } else {
      updateAllPreCln(2, parent.cln)
      updateAllPreCrn(2, parent.cln)
      child.cpi = parent.id
      child.cln = parent.cln + 1
      child.crn = child.cln + 1
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
      (it.crn != null
        && it.cln != null)
    }?.run {
      delete(this)
      deleteAllPreCln(2, cln)
      deleteAllPreCrn(2, cln)
    }
  }
}

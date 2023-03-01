package com.truenine.component.rds.base

import com.truenine.component.core.lang.ContainerUtil
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@NoRepositoryBean
@JvmDefaultWithCompatibility
interface PreSortTreeRepo<T : com.truenine.component.rds.base.PreSortTreeDao, ID : Serializable> :
  BaseRepo<T, ID> {
  @Query(
    """
    from #{#entityName} c
    where c.id = :#{#t.cpi}
    and c.cgu = :#{#t.cgu}
  """
  )
  fun findParentNode(t: T): T


  fun childrenCount(t: T): Long = ((t.crn - t.cln) - 1) / 2

  @Query(
    """
    from #{#entityName} c
    where c.cgu = :#{#ent.cgu}
    and c.cln between :#{#ent.cln} and :#{#ent.crn}
  """
  )
  fun findChildrenNode(@Param("ent") t: T): List<T>

  fun findAllChildrenNodeById(ids: List<ID>): List<T> {
    return ContainerUtil.unfoldNestedListBy {
      findAllById(ids)
        .map { if (!it.isLeafNode) findChildrenNode(it) else mutableListOf() }
    }
  }

  @Query(
    """
    select count(1) + 1
    from #{#entityName} c
    where c.cln < :#{#t.cln}
    and c.crn > :#{#t.crn}
  """
  )
  fun findLevel(t: T): Int

  fun findIsLeaf(t: T): Boolean {
    return t.crn - 1 == t.cln
  }

  @Query(
    """
    update #{#entityName} c
    set c.cln = c.cln + 2
    where c.cln > :p
    and c.cgu = :c
  """
  )
  @Modifying
  fun updatePreCln(p: Long, c: String): Int

  @Query(
    """
    update #{#entityName} c
    set c.crn = c.crn + 2
    where c.crn >= :p
    and c.cgu = :c
  """
  )
  @Modifying
  fun updatePreCrn(p: Long, c: String): Int

  @Query(
    """
    update #{#entityName} c
    set c.cln = c.cln - 2
    where c.cln > :p
    and c.cgu = :c
  """
  )
  @Modifying
  fun deletePreCln(p: Long, c: String): Int

  @Query(
    """
    update #{#entityName} c
    set c.crn = c.crn - 2
    where c.crn >= :p
    and c.cgu = :c
  """
  )
  @Modifying
  fun deletePreCrn(p: Long, c: String): Int


  @Transactional(rollbackFor = [Exception::class])
  fun <P : com.truenine.component.rds.base.PreSortTreeDao> saveChildren(
    t: T,
    p: P
  ): T? = run {
    t.cgu = if (t.cgu != null) t.cgu else p.cgu
    t.cpi = p.id
    t.cln = p.cln + 1
    t.crn = p.crn - 1
    updatePreCln(p.cln, t.cgu)
    updatePreCrn(p.cln, t.cgu)
    save(t)
  }

  @Transactional(rollbackFor = [Exception::class])
  fun deleteNode(t: T) = run {
    t.takeIf {
      it.cgu != null
        && it.crn != null
        && it.cgu != null
    }?.apply {
      delete(this)
      deletePreCln(cln, cgu)
      deletePreCrn(cln, cgu)
    }
  }
}

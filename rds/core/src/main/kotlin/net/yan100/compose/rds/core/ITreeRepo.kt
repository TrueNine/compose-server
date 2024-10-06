/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.core

import net.yan100.compose.core.*
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.entities.ITreeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull

private const val DEPRECATED_TEXT = "接口内部实现方法，不建议调用"

/**
 * # 线索树 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean
interface ITreeRepo<T : ITreeEntity> : IRepo<T> {
  fun findChildrenCount(parent: T): i64 {
    return (parent.rrn - parent.rln - 1) / 2
  }

  /** ## 查询当前节点的所有子节点 */
  @Query(
    """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
  )
  fun findChildren(parent: T): List<T>

  /** ## 分页查询当前节点的所有子节点 */
  @Query(
    """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
  )
  fun findChildren(parent: T, page: Pageable): Page<T>

  /** ## 查询当前节点的直接子集 */
  @Query(
    """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.nlv = :#{#parent.nlv} + 1
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
  )
  fun findDirectChildren(parent: T): List<T>

  /** ## 分页查询当前节点的直接子集 */
  @Query(
    """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.nlv = :#{#parent.nlv} + 1
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
  )
  fun findDirectChildren(parent: T, page: Pageable): Page<T>

  /** 返回当前节点的所有父节点 */
  @Query(
    """
    from #{#entityName} e
    where e.tgi = :#{#child.tgi}
    and e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
  )
  fun findParentPath(child: T): List<T>

  /** 计算当前节点的深度级别 */
  @Query(
    """
    select count(e.id)
    from #{#entityName} e
    where e.tgi = :#{#child.tgi} 
    and e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
  )
  fun findTreeLevel(child: T): i64

  /** 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来 */
  @Query(
    """
    update #{#entityName} e
    set e.rln = e.rln + :rlnOffset
    where e.tgi = :tgi
    and e.rln > :parentRln
  """
  )
  @Modifying
  @Deprecated(DEPRECATED_TEXT, level = DeprecationLevel.ERROR)
  fun pushRlnByOffset(rlnOffset: i64, parentRln: i64, tgi: string?)

  /** 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来 */
  @Query(
    """
    update #{#entityName} e
    set e.rrn = e.rrn + :rrnOffset
    where e.tgi = :tgi 
    and e.rrn >= :parentRln
  """
  )
  @Modifying
  @Deprecated(DEPRECATED_TEXT, level = DeprecationLevel.ERROR)
  fun pushRrnByOffset(rrnOffset: Long, parentRln: Long, tgi: String?)

  /** 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来 */
  @Query(
    """
    update #{#entityName} c
    set c.rln = c.rln - :rlnOffset
    where c.tgi = :tgi
    and c.rln > :parentRln
  """
  )
  @Modifying
  @Deprecated(DEPRECATED_TEXT, level = DeprecationLevel.ERROR)
  fun popRlnByOffset(rlnOffset: Long, parentRln: Long, tgi: String?)

  /** 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来 */
  @Query(
    """
    update #{#entityName} c
    set c.rrn = c.rrn - :rrnOffset
    where c.tgi = :tgi 
    and c.rrn >= :parentRln
  """
  )
  @Modifying
  @Deprecated(DEPRECATED_TEXT, level = DeprecationLevel.ERROR)
  fun popRrnByOffset(rrnOffset: Long, parentRln: Long, tgi: String?): Int

  @ACID
  fun saveChildrenByParentId(parentId: RefId, childrenLazy: () -> List<T>): List<T> {
    return saveChildren(findByIdOrNull(parentId)!!, childrenLazy())
  }

  @ACID
  fun saveChildrenByParentId(parentId: RefId, children: List<T>): List<T> {
    val parent = findByIdOrNull(parentId)
    checkNotNull(parent) { "所选的 parentId 不存在" }
    return saveChildren(parent, children)
  }

  /**
   * 保存一组 树结构，此操作为原子性的 **警告：一次事务只能调用一次**
   *
   * @param parent 父节点
   * @param children 直属子节点列表
   * @return 保存后的子节点
   */
  @ACID
  @Suppress("DEPRECATION_ERROR")
  fun saveChildren(parent: T, children: List<T>): List<T> {
    if (children.isEmpty()) return listOf()
    require(parent.nlv != null && parent.tgi != null) { "父节点缺少必要的值 = $parent" }
    val leftStep = parent.rln + 1
    val offset = (children.size * 2)
    // 更新所有的左节点和右节点
    pushRlnByOffset(offset.toLong(), parent.rln, parent.tgi)
    pushRrnByOffset(offset.toLong(), parent.rln, parent.tgi)
    // 编排并列的子节点
    for (i in 0 until (offset) step 2) {
      val idx = (i / 2)
      children[idx].rpi = parent.id
      children[idx].rln = leftStep + i
      children[idx].rrn = leftStep + i + 1
    }
    return saveAll(children.map {
      it.apply {
        rpi = parent.id
        nlv = parent.nlv + 1
        tgi = parent.tgi
      }
    })
  }

  /** 对 saveChildren 的尾随闭包调用 **警告：一次事务只能调用一次** */
  @ACID
  fun saveChildren(parent: T, children: () -> List<T>): List<T> {
    return saveChildren(parent, children())
  }

  /** 保存单个子节点 **警告：一次事务只能调用一次** */
  @ACID
  @Suppress("DEPRECATION_ERROR")
  fun saveChild(parent: T? = null, child: T): T {
    return if (parent == null) {
      child.toNewEntity()
      save(child)
    } else {
      pushRlnByOffset(2, parent.rln, parent.tgi)
      pushRrnByOffset(2, parent.rln, parent.tgi)
      child.rpi = parent.id
      child.tgi = parent.tgi
      child.rln = parent.rln + 1
      child.rrn = child.rln + 1
      save(child.apply { nlv = parent.nlv + 1 })
    }
  }

  /** **警告：一次事务只能调用一次** */
  @ACID
  @Suppress("DEPRECATION_ERROR")
  fun deleteChild(child: T) {
    delete(child)
    popRlnByOffset(2, child.rln, child.tgi)
    popRrnByOffset(2, child.rln, child.tgi)
  }

  @Query("from #{#entityName} e where e.nlv = :level")
  fun findByNlv(level: Long, page: Pageable): Page<T>
  fun findByNlv(level: Long, page: Pq): Pr<T> = findByNlv(level, page.toPageable()).toPr()
}

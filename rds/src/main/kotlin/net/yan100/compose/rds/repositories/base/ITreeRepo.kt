package net.yan100.compose.rds.repositories.base

import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.annotations.BetaTest
import net.yan100.compose.rds.core.entities.TreeEntity
import net.yan100.compose.rds.entities.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional


/**
 * # 线索树 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean
interface ITreeRepo<T : TreeEntity> : IRepo<T> {
    fun findChildrenCount(parent: T): BigSerial {
        require(parent.rln != null) {
            "父节点：$parent 左节点为 null"
        }
        require(parent.rrn != null) {
            "父节点：$parent 右节点为 null"
        }
        return (parent.rrn - parent.rln - 1) / 2
    }

    /**
     * ## 查询当前节点的所有子节点
     */
    @Query(
        """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
    )
    fun findChildren(parent: T): List<T>

    /**
     * ## 分页查询当前节点的所有子节点
     */
    @Query(
        """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
    )
    fun findChildren(parent: T, page: Pageable): Page<T>

    /**
     * ## 查询当前节点的直接子集
     */
    @Query(
        """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.nlv = :#{#parent.nlv} + 1
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
    )
    fun findDirectChildren(parent: T): List<T>

    /**
     * ## 分页查询当前节点的直接子集
     */
    @Query(
        """
    from #{#entityName} e
    where e.tgi = :#{#parent.tgi}
    and e.nlv = :#{#parent.nlv} + 1
    and e.rln between (:#{#parent.rln}+1) and :#{#parent.rrn}
  """
    )
    fun findDirectChildren(parent: T, page: Pageable): Page<T>


    /**
     * 返回当前节点的所有父节点
     */
    @Query(
        """
    from #{#entityName} e
    where e.tgi = :#{#child.tgi}
    and e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
    )
    fun findParentPath(child: T): List<T>

    /**
     * 计算当前节点的深度级别
     */
    @Query(
        """
    select count(1)
    from #{#entityName} e
    where e.tgi = :#{#child.tgi} 
    and e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
    )
    fun findTreeLevel(child: T): BigSerial

    /**
     * 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来
     */
    @Query(
        """
    update #{#entityName} e
    set e.rln = e.rln + :rlnOffset
    where e.tgi = :tgi
    and e.rln > :parentRln
  """
    )
    @Modifying
    fun pushRlnByOffset(rlnOffset: BigSerial, parentRln: BigSerial, tgi: SerialCode?)

    /**
     * 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来
     */
    @Query(
        """
    update #{#entityName} e
    set e.rrn = e.rrn + :rrnOffset
    where e.tgi = :tgi 
    and e.rrn >= :parentRln
  """
    )
    @Modifying
    fun pushRrnByOffset(rrnOffset: Long, parentRln: Long, tgi: String?)

    /**
     * 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来
     */
    @Query(
        """
    update #{#entityName} c
    set c.rln = c.rln - :rlnOffset
    where c.tgi = :tgi
    and c.rln > :parentRln
  """
    )
    @Modifying
    fun popRlnByOffset(rlnOffset: Long, parentRln: Long, tgi: String?)

    /**
     * 此方法不建议调用，为内部更新时的计算方法，由于接口只能公开的限制，只能暴露了出来
     */
    @Query(
        """
    update #{#entityName} c
    set c.rrn = c.rrn - :rrnOffset
    where c.tgi = :tgi 
    and c.rrn >= :parentRln
  """
    )
    @Modifying
    fun popRrnByOffset(rrnOffset: Long, parentRln: Long, tgi: String?): Int

    @Transactional(rollbackFor = [Exception::class])
    fun saveChildrenByParentId(
        parentId: String,
        childrenLazy: () -> List<T>
    ): List<T> {
        return saveChildren(findByIdOrNull(parentId)!!, childrenLazy())
    }

    @Transactional(rollbackFor = [Exception::class])
    fun saveChildrenByParentId(
        parentId: String,
        children: List<T>
    ): List<T> {
        val parent = findByIdOrNull(parentId)
        checkNotNull(parent) { "所选的 parentId 不存在" }
        return saveChildren(parent, children)
    }

    /**
     * 保存一组 树结构，此操作为原子性的
     * **警告：一次事务只能调用一次**
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
                && parent.nlv != null
                && parent.tgi != null
        ) { "父节点缺少必要的值 = $parent" }
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
                nlv = parent.nlv!! + 1
                tgi = parent.tgi
            }
        })
    }

    /**
     * 对 saveChildren 的尾随闭包调用
     * **警告：一次事务只能调用一次**
     */
    @Transactional(rollbackFor = [Exception::class])
    fun saveChildren(
        parent: T,
        children: () -> List<T>
    ): List<T> {
        return saveChildren(parent, children())
    }

    /**
     * 保存单个子节点
     * **警告：一次事务只能调用一次**
     */
    @Transactional(rollbackFor = [Exception::class])
    fun saveChild(
        parent: T? = null,
        child: T
    ): T {
        return if (parent == null) {
            child.asNew()
            save(child)
        } else {
            pushRlnByOffset(2, parent.rln, parent.tgi)
            pushRrnByOffset(2, parent.rln, parent.tgi)
            child.rpi = parent.id
            child.tgi = parent.tgi
            child.rln = parent.rln + 1
            child.rrn = child.rln + 1
            save(child.apply {
                nlv = parent.nlv!! + 1
            })
        }
    }

    /**
     * **警告：一次事务只能调用一次**
     */
    @BetaTest
    @Transactional(rollbackFor = [Exception::class])
    fun deleteChild(child: T) {
        delete(child)
        popRlnByOffset(2, child.rln, child.tgi)
        popRrnByOffset(2, child.rln, child.tgi)
    }

    @Query("from #{#entityName} e where e.nlv = :level")
    fun findByNodeLevel(level: Long, page: Pageable): Page<Address>
}


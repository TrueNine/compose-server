package net.yan100.compose.rds.base

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.annotations.BetaTest
import net.yan100.compose.core.annotations.BigIntegerAsString
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.rds.annotations.BizCode
import net.yan100.compose.rds.entity.Address
import net.yan100.compose.rds.util.Pq
import net.yan100.compose.rds.util.Pr
import net.yan100.compose.rds.util.Pw
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
  JpaRepository<T, String>,
  CrudRepository<T, String>,
  JpaSpecificationExecutor<T>

@NoRepositoryBean
@JvmDefaultWithCompatibility
interface BaseRepository<T : BaseEntity> : AnyRepository<T> {

  fun findByIdAndNotLogicDelete(id: String): T = findByIdAndNotLogicDeleteOrNull(id)!!

  @Query("FROM #{#entityName} e WHERE e.id = :id AND e.ldf = false")
  fun findByIdAndNotLogicDeleteOrNull(id: String): T?

  @Query("FROM #{#entityName} e WHERE e.ldf = false")
  fun findAllByNotLogicDeleted(page: Pageable): Page<T>

  @Query("FROM #{#entityName} e WHERE e.id IN :ids AND (e.ldf = false OR e.ldf IS null)")
  fun findAllByIdAndNotLogicDeleted(ids: List<String>, page: Pageable): Page<T>

  @Query("SELECT (e.ldf = false) FROM #{#entityName} e WHERE e.id = :id")
  fun findLdfById(id: String): Boolean?

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteById(id: String): T? = findByIdOrNull(id)?.let { it.ldf = true;save(it) }

  @Transactional(rollbackFor = [Exception::class])
  fun logicDeleteAllById(ids: List<String>): List<T> = findAllById(ids).filter { !it.ldf!! }.apply { saveAll(this) }

  @Query("SELECT count(e.id) FROM #{#entityName} e WHERE e.ldf = false OR e.ldf IS null")
  fun countByNotLogicDeleted(): Long

  @Query("SELECT count(e.id) > 0 FROM #{#entityName} e WHERE e.id = :id AND (e.ldf = false OR e.ldf IS null)")
  fun existsByIdAndNotLogicDeleted(id: String)

  @Query("SELECT e.rlv FROM #{#entityName} e WHERE e.id = :id")
  fun findRlvById(id: String): Long

  fun modifyWrapper(e: T): T {
    return if (e.id != null) e.also { it.rlv = findRlvById(e.id!!) }
    else e
  }
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
    require(parent.rln != null) {
      "父节点：$parent 左节点为 null"
    }
    require(parent.rrn != null) {
      "父节点：$parent 右节点为 null"
    }
    return (parent.rrn!! - parent.rln!! - 1) / 2
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
    select count(1) + 1
    from #{#entityName} e
    where e.tgi = :#{#child.tgi} 
    and e.rln < :#{#child.rln}
    and e.rrn > :#{#child.rrn}
  """
  )
  fun findTreeLevel(child: T): Long

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
  fun pushRlnByOffset(rlnOffset: Long, parentRln: Long, tgi: String?)

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
    val leftStep = parent.rln!! + 1
    val offset = (children.size * 2)
    // 更新所有的左节点和右节点
    pushRlnByOffset(offset.toLong(), parent.rln!!, parent.tgi)
    pushRrnByOffset(offset.toLong(), parent.rln!!, parent.tgi)
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
  ): List<T> = saveChildren(parent, children())

  /**
   * 保存单个子节点
   * **警告：一次事务只能调用一次**
   */
  @Transactional(rollbackFor = [Exception::class])
  fun saveChild(
    parent: T?,
    child: T
  ): T {
    return if (parent == null) {
      child.asNew()
      save(child)
    } else {
      pushRlnByOffset(2, parent.rln!!, parent.tgi)
      pushRrnByOffset(2, parent.rln!!, parent.tgi)
      child.rpi = parent.id
      child.tgi = parent.tgi
      child.rln = parent.rln!! + 1
      child.rrn = child.rln!! + 1
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
    popRlnByOffset(2, child.rln!!, child.tgi)
    popRrnByOffset(2, child.rln!!, child.tgi)
  }

  @Query("from #{#entityName} e where e.nlv = :level")
  fun findByNodeLevel(level: Long, page: Pageable): Page<Address>
}


/**
 * # 单一 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
interface BaseService<T : AnyEntity> {
  fun findAll(page: Pq? = Pw.DEFAULT_MAX): Pr<T>
  fun findAllByNotLogicDeleted(page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun findById(id: String): T?
  fun findAllById(ids: List<String>): MutableList<T>
  fun findByIdAndNotLogicDeleted(id: String): T
  fun findByIdAndNotLogicDeletedOrNull(id: String): T?

  fun findAllByIdAndNotLogicDeleted(ids: List<String>, page: Pq? = Pw.DEFAULT_MAX): Pr<T>

  fun countAll(): Long
  fun countAllByNotLogicDeleted(): Long
  fun existsById(id: String): Boolean

  fun findLdfById(id: String): Boolean

  fun save(e: T): T
  fun saveAll(es: List<T>): List<T>

  fun deleteById(id: String)
  fun deleteAllById(ids: List<String>)

  fun logicDeleteById(id: String): T?
  fun logicDeleteAllById(ids: List<String>): List<T>
}


/**
 * 预排序树
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@MappedSuperclass
open class TreeEntity : BaseEntity() {
  /**
   * 父id
   */
  @JsonIgnore
  @Column(name = RPI)
  @Schema(title = "父id")
  open var rpi: String? = null

  /**
   * 左节点
   */
  @JsonIgnore
  @BigIntegerAsString
  @Column(name = RLN)
  @Schema(title = "左节点", hidden = true)
  open var rln: Long? = null

  /**
   * 右节点
   */
  @JsonIgnore
  @BigIntegerAsString
  @Column(name = RRN)
  @Schema(title = "右节点", hidden = true)
  open var rrn: Long? = null

  /**
   * 节点级别
   */
  @JsonIgnore
  @BigIntegerAsString
  @Schema(title = "节点级别", defaultValue = "0")
  @Column(name = NLV)
  open var nlv: Long? = 0L

  /**
   * ### 树组 id，在节点插入时必须更上，在插入时随着父id进行更改
   */
  @BizCode
  @JsonIgnore
  @Schema(title = "树 组id", defaultValue = "0")
  @Column(name = TGI)
  open var tgi: String? = null

  companion object {
    const val RPI = DataBaseBasicFieldNames.PARENT_ID
    const val RLN = DataBaseBasicFieldNames.LEFT_NODE
    const val RRN = DataBaseBasicFieldNames.RIGHT_NODE
    const val NLV = DataBaseBasicFieldNames.NODE_LEVEL
    const val TGI = DataBaseBasicFieldNames.TREE_GROUP_ID
  }

  override fun asNew() {
    super.asNew()
    this.rln = 1L
    this.rrn = 2L
    this.nlv = 0
    this.rpi = null
  }
}

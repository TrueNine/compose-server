package net.yan100.compose.rds.entities

import jakarta.persistence.*
import net.yan100.compose.Id
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.datetime
import net.yan100.compose.i64
import net.yan100.compose.rds.listeners.BizCodeInsertListener
import net.yan100.compose.rds.listeners.SnowflakeIdInsertListener
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

/**
 * ## 合并从数据库内查询的实体
 *
 * @param target 需合并对象
 * @param findByIdFn 查询函数
 * @param preMergeFn 合并前处理函数
 */
@Suppress("DEPRECATION_ERROR")
fun <T : IJpaEntity> T.merge(
  target: T,
  findByIdFn: (id: Id) -> T?,
  preMergeFn: (dbData: T, thisData: T) -> T = { _, h -> h },
): T {
  return takeUpdate {
    val queryEntity = findByIdFn(target.id)
    checkNotNull(queryEntity) { "未找到修改的数据版本" }
    target.rlv = queryEntity.rlv
    target.mrd = datetime.now()
    preMergeFn(queryEntity, target)
  } ?: throw IllegalArgumentException("未找到修改的数据")
}

@Suppress("DEPRECATION_ERROR")
fun <T : IJpaEntity> mergeAll(
  targets: List<T>,
  findAllByIdFn: (ids: List<Id>) -> List<T>,
  checkLength: Boolean = true,
  preMergeFn: (dbData: T, thisData: T) -> T = { _, h -> h },
): List<T> {
  val errMsg = "需更新的长度不一致"
  val prepared = targets.filterNot { it.isNew }
  if (checkLength) check(targets.size == prepared.size) { errMsg }

  val dbDataEntities = findAllByIdFn(prepared.map { it.id })
  if (checkLength) check(dbDataEntities.size == prepared.size) { errMsg }

  val pd = dbDataEntities.associateBy { prepared.find { d -> it.id == d.id }!! }
  val allSave =
    pd.map {
      val p = it.key
      val d = it.value
      p.rlv = d.rlv
      p.mrd = datetime.now()
      preMergeFn(d, p)
    }
  if (checkLength) check(allSave.size == prepared.size) { "需更新的长度不一致" }
  return allSave
}

/**
 * ## 合并从数据库内查询的数据
 *
 * @param target 数据库实体
 * @param merge 合并函数：(this 自身实体, db 数据库实体) -> 默认合并的自身实体
 */
@Suppress("DEPRECATION_ERROR")
inline fun <T : IJpaEntity> T.fromDbData(
  target: T,
  crossinline merge: T.(w: T) -> T = { it },
): T {
  check(!target.isNew) { "要合并的实体必须为数据库内查询的实体" }
  id = target.id
  rlv = target.rlv
  crd = target.crd
  mrd = datetime.now()
  return merge(target, this)
}

@EntityListeners(BizCodeInsertListener::class, SnowflakeIdInsertListener::class)
@Access(AccessType.PROPERTY)
@MappedSuperclass
open class IEntityDelegate : IAnyEntityDelegate(), IJpaEntity {
  @Version
  @Column(name = IDbNames.ROW_LOCK_VERSION)
  override var rlv: i64? = null

  @CreatedDate
  @Column(name = IDbNames.CREATE_ROW_DATETIME)
  override var crd: datetime? = null

  @LastModifiedDate
  @Column(name = IDbNames.MODIFY_ROW_DATETIME)
  override var mrd: datetime? = null

  @Column(name = IDbNames.LOGIC_DELETE_FLAG)
  override var ldf: Boolean? = null
}

fun entity(): IJpaEntity {
  return IEntityDelegate()
}

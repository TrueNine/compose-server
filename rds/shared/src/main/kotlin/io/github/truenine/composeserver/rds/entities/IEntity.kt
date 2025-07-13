package io.github.truenine.composeserver.rds.entities

import io.github.truenine.composeserver.datetime
import org.babyfish.jimmer.sql.*

@MappedSuperclass
interface IEntity : IPersistentEntity {
  /** 字段创建时间 */
  @Column(name = "crd") val crd: datetime?

  /** 字段修改时间 */
  @Column(name = "mrd") val mrd: datetime?

  /** 乐观锁版本号 */
  @Version @Default("0") @Column(name = "rlv") val rlv: Int

  /** 逻辑删除标志 */
  @LogicalDeleted("now") @Column(name = "ldf") val ldf: datetime?
}

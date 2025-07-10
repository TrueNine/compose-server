package net.yan100.compose.rds.entities

import net.yan100.compose.datetime
import net.yan100.compose.i32
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Default
import org.babyfish.jimmer.sql.LogicalDeleted
import org.babyfish.jimmer.sql.MappedSuperclass
import org.babyfish.jimmer.sql.Version

@MappedSuperclass
interface IEntity : IPersistentEntity {
  /** 字段创建时间 */
  @Column(name = "crd") val crd: datetime?

  /** 字段修改时间 */
  @Column(name = "mrd") val mrd: datetime?

  /** 乐观锁版本号 */
  @Version @Default("0") @Column(name = "rlv") val rlv: i32

  /** 逻辑删除标志 */
  @LogicalDeleted("now") @Column(name = "ldf") val ldf: datetime?
}

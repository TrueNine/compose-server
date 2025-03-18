package net.yan100.compose.rds.core.entities

import net.yan100.compose.core.datetime
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IJimmerEntity : IJimmerPersistentEntity {
  /** 字段创建时间 */
  @Column(name = "crd", sqlType = "datetime") val crd: datetime?

  /** 字段修改时间 */
  @Column(name = "mrd", sqlType = "datetime") val mrd: datetime?

  /** 乐观锁版本号 */
  @Column(name = "rlv", sqlType = "bigint") val rlv: Long?

  /** 逻辑删除标志 */
  @Column(name = "ldf", sqlType = "boolean") val ldf: Boolean?
}

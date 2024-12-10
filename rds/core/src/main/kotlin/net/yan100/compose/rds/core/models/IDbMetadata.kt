package net.yan100.compose.rds.core.models

import net.yan100.compose.core.datetime
import org.babyfish.jimmer.sql.Embeddable

/**
 * 数据库原字段映射
 */
@Embeddable
interface IDbMetadata {
  val crd: datetime
  val mrd: datetime?
  val rlv: Long?
  val ldf: Boolean?
}

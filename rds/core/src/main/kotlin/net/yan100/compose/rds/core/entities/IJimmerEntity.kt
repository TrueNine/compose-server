package net.yan100.compose.rds.core.entities

import net.yan100.compose.rds.core.models.IDbMetadata
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IJimmerEntity : IJimmerPersistentEntity {
  /**
   * 数据库内元数据
   *
   * > 不能传入，也不会输出，不应干预
   */
  val databaseMetadata: IDbMetadata?
}

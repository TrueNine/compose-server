package net.yan100.compose.rds.core.entities

import net.yan100.compose.rds.core.entities.embeddable.DbTreeMetadata
import org.babyfish.jimmer.sql.MappedSuperclass

@Deprecated("树结构已废弃")
@MappedSuperclass
interface IJimmerTreeEntity : IJimmerEntity {
  /**
   * 树节点元数据
   * > 不能传入，也不会输出，不应干预
   */
  @Deprecated("树结构已废弃")
  val databaseTreeMetadata: DbTreeMetadata?
}

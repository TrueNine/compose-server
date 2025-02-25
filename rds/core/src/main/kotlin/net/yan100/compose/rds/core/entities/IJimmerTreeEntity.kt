package net.yan100.compose.rds.core.entities

import net.yan100.compose.rds.core.models.IDbTreeMetadata
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IJimmerTreeEntity : IJimmerEntity {
  /**
   * 树节点元数据
   * > 不能传入，也不会输出，不应干预
   */
  val databaseTreeMetadata: IDbTreeMetadata?
}

package net.yan100.compose.rds.core.entities

import net.yan100.compose.rds.core.converters.jimmer.NullDbTreeMetadataConverter
import net.yan100.compose.rds.core.models.IDbTreeMetadata
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IJimmerTreeEntity : IJimmerEntity {
  /**
   * 树节点元数据
   *
   * > 不能传入，也不会输出，不应干预
   */
  @JsonConverter(NullDbTreeMetadataConverter::class)
  val databaseTreeMetadata: IDbTreeMetadata?
}

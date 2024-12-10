package net.yan100.compose.rds.core.entities

import net.yan100.compose.rds.core.models.IDbTreeMetadata
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IJimmerTreeEntity : IJimmerEntity {
  val databaseTreeMetadata: IDbTreeMetadata
}

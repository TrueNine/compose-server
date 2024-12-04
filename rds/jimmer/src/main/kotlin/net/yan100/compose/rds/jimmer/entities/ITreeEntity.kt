package net.yan100.compose.rds.jimmer.entities

import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.i64
import net.yan100.compose.core.string
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface ITreeEntity : IEntity {
  @Column(name = IDbNames.TREE_LEFT_NODE)
  val rln: i64

  @Column(name = IDbNames.TREE_RIGHT_NODE)
  val rrn: i64

  @Column(name = IDbNames.TREE_NODE_LEVEL)
  val nlv: i64

  @Column(name = IDbNames.TREE_GROUP_ID)
  val tgi: string?
}

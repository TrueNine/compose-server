package net.yan100.compose.rds.core.entities

import jakarta.persistence.*
import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.i64
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.listeners.BizCodeInsertListener
import net.yan100.compose.rds.core.listeners.SnowflakeIdInsertListener

@EntityListeners(
  BizCodeInsertListener::class,
  SnowflakeIdInsertListener::class,
)
@Access(AccessType.PROPERTY)
@MappedSuperclass
open class ITreeEntityDelegate : IEntityDelegate(), IJpaTreeEntity {
  @Column(name = IDbNames.ROW_PARENT_ID, nullable = true)
  override var rpi: RefId? = null

  @Column(name = IDbNames.TREE_LEFT_NODE)
  override var rln: i64 = 1

  @Column(name = IDbNames.TREE_RIGHT_NODE)
  override var rrn: i64 = 2

  @Column(name = IDbNames.TREE_NODE_LEVEL)
  override var nlv: i64 = 0

  @Column(name = IDbNames.TREE_GROUP_ID, nullable = true)
  override var tgi: string? = null
}

fun treeEntity(): IJpaTreeEntity {
  return ITreeEntityDelegate()
}

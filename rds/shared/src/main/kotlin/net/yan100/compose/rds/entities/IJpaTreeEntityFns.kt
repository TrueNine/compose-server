package net.yan100.compose.rds.entities

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.RefId
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.i64
import net.yan100.compose.rds.listeners.BizCodeInsertListener
import net.yan100.compose.rds.listeners.SnowflakeIdInsertListener
import net.yan100.compose.string

@EntityListeners(BizCodeInsertListener::class, SnowflakeIdInsertListener::class)
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

package net.yan100.compose.rds.core.domain

import net.yan100.compose.core.*
import java.io.Serializable

open class PersistenceAuditTreeData(
  val leftNodeNo: i64,
  val rightNodeNo: i64,
  val nodeLevel: i64,
  val treeGroupId: string?,
  val parentId: Id?,
  override val shadowRemoved: bool,
  override val lockVersion: i64,
  override val id: Id,
  override val createdAt: datetime,
  override val updatedAt: datetime
) : PersistenceAuditData(shadowRemoved, lockVersion, id, createdAt, updatedAt), Serializable {
  override fun toString(): String {
    return "${super.toString()}, leftNodeNo=$leftNodeNo, rightNodeNo=$rightNodeNo, nodeLevel=$nodeLevel, treeGroupId=$treeGroupId, parentId=$parentId"
  }
}

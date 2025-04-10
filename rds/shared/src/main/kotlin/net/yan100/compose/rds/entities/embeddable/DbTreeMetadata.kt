package net.yan100.compose.rds.entities.embeddable

import net.yan100.compose.consts.IDbNames
import net.yan100.compose.i64
import net.yan100.compose.string
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Embeddable

@Deprecated("难于维护")
@Embeddable
interface DbTreeMetadata {
  /** 左节点 */
  @Column(name = IDbNames.TREE_LEFT_NODE, sqlType = "bigint") val rln: i64

  /** 右节点 */
  @Column(name = IDbNames.TREE_RIGHT_NODE, sqlType = "bigint") val rrn: i64

  /** 节点深度 */
  @Column(name = IDbNames.TREE_NODE_LEVEL, sqlType = "bigint") val nlv: i64

  /** 组 id */
  @Column(name = IDbNames.TREE_GROUP_ID, sqlType = "varchar") val tgi: string?
}

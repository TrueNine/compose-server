package net.yan100.compose.rds.core.entities

import jakarta.persistence.Column
import jakarta.persistence.Transient
import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.i64
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaAutoManagement
import net.yan100.compose.rds.core.annotations.OrderCode

/**
 * 预排序树
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@Deprecated("树结构已废弃")
interface IJpaTreeEntity : IJpaEntity {

  /** 父id */
  @get:Column(name = RPI) @get:MetaAutoManagement var rpi: RefId?

  /** 左节点 */
  @get:Column(name = RLN) @get:MetaAutoManagement var rln: i64

  /** 右节点 */
  @get:Column(name = RRN) @get:MetaAutoManagement var rrn: i64

  /** 节点级别 */
  @get:Column(name = NLV) @get:MetaAutoManagement var nlv: i64

  /** ### 树组 id，在节点插入时必须更上，在插入时随着父id进行更改 */
  @get:OrderCode
  @get:Column(name = TGI)
  @get:MetaAutoManagement
  var tgi: string?

  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    this.rln = 0L
    this.rrn = 0L
    this.nlv = 0L
    this.tgi = null
    rpi = null
    recordChangedSensitiveData()
  }

  @Transient
  override fun toNewEntity() {
    super.toNewEntity()
    rln = 1L
    rrn = 2L
    nlv = 0
    tgi = IDbNames.Rbac.ROOT_ID_STR
    rpi = null
  }

  companion object {
    const val RPI = IDbNames.ROW_PARENT_ID
    const val RLN = IDbNames.TREE_LEFT_NODE
    const val RRN = IDbNames.TREE_RIGHT_NODE
    const val NLV = IDbNames.TREE_NODE_LEVEL
    const val TGI = IDbNames.TREE_GROUP_ID
  }
}

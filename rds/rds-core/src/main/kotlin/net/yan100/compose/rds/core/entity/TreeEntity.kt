package net.yan100.compose.rds.core.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.annotations.BigIntegerAsString
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.rds.core.annotations.BizCode

/**
 * 预排序树
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@MappedSuperclass
open class TreeEntity : BaseEntity() {
  companion object {
    const val RPI = DataBaseBasicFieldNames.PARENT_ID
    const val RLN = DataBaseBasicFieldNames.LEFT_NODE
    const val RRN = DataBaseBasicFieldNames.RIGHT_NODE
    const val NLV = DataBaseBasicFieldNames.NODE_LEVEL
    const val TGI = DataBaseBasicFieldNames.TREE_GROUP_ID
  }

  /**
   * 父id
   */
  @JsonIgnore
  @Column(name = RPI)
  @Schema(title = "父id")
  open var rpi: String? = null

  /**
   * 左节点
   */
  @JsonIgnore
  @BigIntegerAsString
  @Column(name = RLN)
  @Schema(title = "左节点", hidden = true)
  open var rln: Long? = null

  /**
   * 右节点
   */
  @JsonIgnore
  @BigIntegerAsString
  @Column(name = RRN)
  @Schema(title = "右节点", hidden = true)
  open var rrn: Long? = null

  /**
   * 节点级别
   */
  @JsonIgnore
  @BigIntegerAsString
  @Schema(title = "节点级别", defaultValue = "0")
  @Column(name = NLV)
  open var nlv: Long? = 0L

  /**
   * ### 树组 id，在节点插入时必须更上，在插入时随着父id进行更改
   */
  @BizCode
  @JsonIgnore
  @Schema(title = "树 组id", defaultValue = "0")
  @Column(name = TGI)
  open var tgi: String? = null

  override fun asNew() {
    super.asNew()
    this.rln = 1L
    this.rrn = 2L
    this.nlv = 0
    this.tgi = DataBaseBasicFieldNames.Rbac.ROOT_ID_STR
    this.rpi = null
  }

  override fun toString(): String {
    return withToString(
      super.toString(),
      "rpi" to rpi,
      "rln" to rln,
      "rrn" to rrn,
      "nlv" to nlv,
      "tgi" to tgi
    )
  }
}

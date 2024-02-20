package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
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
abstract class TreeEntity : IEntity() {
    /**
     * 父id
     */
    @JsonIgnore
    @Column(name = RPI)
    @Schema(title = "父id")
    var rpi: RefId? = null

    /**
     * 左节点
     */
    @JsonIgnore
    @BigIntegerAsString
    @Column(name = RLN)
    @Schema(title = "左节点", hidden = true)
    var rln: BigSerial = 1L

    /**
     * 右节点
     */
    @JsonIgnore
    @BigIntegerAsString
    @Column(name = RRN)
    @Schema(title = "右节点", hidden = true)
    var rrn: BigSerial = 2L

    /**
     * 节点级别
     */
    @JsonIgnore
    @BigIntegerAsString
    @Schema(title = "节点级别", defaultValue = "0")
    @Column(name = NLV)
    var nlv: BigSerial? = 0L

    /**
     * ### 树组 id，在节点插入时必须更上，在插入时随着父id进行更改
     */
    @BizCode
    @Nullable
    @JsonIgnore
    @Column(name = TGI)
    @Schema(title = "树 组id", defaultValue = "0")
    var tgi: SerialCode? = null


    override fun asNew() {
        super.asNew()
        rln = 1L
        rrn = 2L
        nlv = 0
        tgi = DataBaseBasicFieldNames.Rbac.ROOT_ID_STR
        rpi = null
    }

    override fun toString(): String {
        return withToString(
            toString(),
            RPI to rpi,
            RLN to rln,
            RRN to rrn,
            NLV to nlv,
            TGI to tgi
        )
    }

    companion object {
        const val RPI = DataBaseBasicFieldNames.PARENT_ID
        const val RLN = DataBaseBasicFieldNames.LEFT_NODE
        const val RRN = DataBaseBasicFieldNames.RIGHT_NODE
        const val NLV = DataBaseBasicFieldNames.NODE_LEVEL
        const val TGI = DataBaseBasicFieldNames.TREE_GROUP_ID
    }
}

/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.i64
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.annotations.OrderCode
import net.yan100.compose.rds.core.domain.PersistenceAuditTreeData

/**
 * 预排序树
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@MappedSuperclass
abstract class ITreeEntity : IEntity() {
  companion object {
    @kotlin.jvm.Transient
    const val RPI = IDbNames.ROW_PARENT_ID

    @kotlin.jvm.Transient
    const val RLN = IDbNames.LEFT_NODE

    @kotlin.jvm.Transient
    const val RRN = IDbNames.RIGHT_NODE

    @kotlin.jvm.Transient
    const val NLV = IDbNames.NODE_LEVEL

    @kotlin.jvm.Transient
    const val TGI = IDbNames.TREE_GROUP_ID
  }

  /**
   * ## 当前数据的审计数据，独特于 ITreeEntity
   */
  @get:JsonIgnore
  @get:Transient
  @get:Schema(hidden = true)
  override val dbEntityAuditData: PersistenceAuditTreeData?
    get() = if (isNew) null
    else PersistenceAuditTreeData(
      leftNodeNo = rln,
      rightNodeNo = rrn,
      nodeLevel = nlv,
      treeGroupId = tgi,
      parentId = rpi,
      shadowRemoved = dbEntityShadowRemoveTag,
      lockVersion = dbEntityRowLockVersion,
      id = id,
      createdAt = dbEntityCreatedDatetime,
      updatedAt = dbEntityLastModifyDatetime
    )


  /** 父id */
  @JsonIgnore
  @Column(name = RPI)
  @Schema(title = "父id")
  var rpi: RefId? = null

  /** 左节点 */
  @JsonIgnore
  @Column(name = RLN)
  @Schema(title = "左节点", hidden = true)
  var rln: i64 = 1L

  /** 右节点 */
  @JsonIgnore
  @Column(name = RRN)
  @Schema(title = "右节点", hidden = true)
  var rrn: i64 = 2L

  /** 节点级别 */
  @JsonIgnore
  @Schema(title = "节点级别", defaultValue = "0")
  @Column(name = NLV)
  var nlv: i64 = 0L

  /** ### 树组 id，在节点插入时必须更上，在插入时随着父id进行更改 */
  @OrderCode
  @JsonIgnore
  @Column(name = TGI)
  @Schema(title = "树 组id", defaultValue = "0")
  var tgi: string? = null

  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    this.rln = 0L
    this.rrn = 0L
    this.nlv = 0L
    this.tgi = null
    rpi = null
    recordChangedSensitiveData()
  }


  @JsonIgnore
  @Transient
  override fun toNewEntity() {
    super.toNewEntity()
    rln = 1L
    rrn = 2L
    nlv = 0
    tgi = IDbNames.Rbac.ROOT_ID_STR
    rpi = null
  }
}

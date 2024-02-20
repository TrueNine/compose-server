/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.annotation.Nullable
import jakarta.persistence.*
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

/**
 * api
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "api")
@Table(name = Api.TABLE_NAME)
abstract class Api : IEntity() {
  companion object {
    const val TABLE_NAME = "api"
    const val NAME = "name"
    const val DOC = "doc"
    const val PERMISSIONS_ID = "permissions_id"
    const val API_PATH = "api_path"
    const val API_METHOD = "api_method"
    const val API_PROTOCOL = "api_protocol"
  }

  /** 名称 */
  @Schema(title = "名称") @Column(name = NAME) @Nullable var name: String? = null

  /** 描述 */
  @Schema(title = "描述") @Column(name = DOC) @Nullable var doc: String? = null

  /** 权限 */
  @Nullable
  @Schema(title = "权限", requiredMode = RequiredMode.NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(
    name = PERMISSIONS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  @NotFound(action = NotFoundAction.IGNORE)
  var permissions: Permissions? = null

  /** 路径 */
  @Schema(title = "路径") @Column(name = API_PATH) lateinit var apiPath: String

  /** 请求方式 */
  @Schema(title = "请求方式") @Column(name = API_METHOD) lateinit var apiMethod: String

  /** 请求协议 */
  @Schema(title = "请求协议") @Column(name = API_PROTOCOL) lateinit var apiProtocol: String
}

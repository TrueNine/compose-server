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
 * API请求记录
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "API请求记录")
@Table(name = ApiCallRecordIEntity.TABLE_NAME)
class ApiCallRecordIEntity : IEntity() {
  companion object {
    const val TABLE_NAME = "api_call_record"

    const val API_ID = "api_id"
    const val DEVICE_CODE = "device_code"
    const val REQ_IP = "req_ip"
    const val RESP_CODE = "resp_code"
    const val RESP_RESULT_ENC = "resp_result_enc"
    const val LOGIN_IP = "login_ip"
  }

  /** 从属 API */
  @Schema(title = "API", requiredMode = RequiredMode.NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(name = API_ID, referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @NotFound(action = NotFoundAction.IGNORE)
  var api: Api? = null

  /** 设备 id, 浏览器为 agent */
  @Nullable @Schema(title = "设备 id, 浏览器为 agent") @Column(name = DEVICE_CODE) var deviceCode: String? = null

  /** 请求 ip */
  @Nullable @Schema(title = "请求 ip") @Column(name = REQ_IP) var reqIp: String? = null

  /** 登录 ip */
  @Nullable @Schema(title = "登录 ip") @Column(name = LOGIN_IP) var loginIp: String? = null

  /** 响应码 */
  @Nullable @Schema(title = "响应码") @Column(name = RESP_CODE) var respCode: Int? = null

  /** 请求结果 */
  @Nullable @Schema(title = "请求结果") @Column(name = RESP_RESULT_ENC) var respResultEnc: String? = null
}

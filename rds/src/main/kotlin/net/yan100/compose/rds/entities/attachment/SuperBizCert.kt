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
package net.yan100.compose.rds.entities.attachment

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.alias.*
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity
import java.time.LocalDate

@MetaDef
@MappedSuperclass
abstract class SuperBizCert : IEntity() {

  @get:Schema(title = "用户信息id")
  abstract var userInfoId: RefId?

  @get:Schema(title = "签发时间")
  abstract var issueDate: LocalDate?

  @get:Schema(title = "地址详情")
  abstract var addressDetailsId: ReferenceId?

  @get:Schema(title = "注册地")
  abstract var addressCode: SerialCode?

  @get:Schema(title = "经营范围")
  abstract var bizRange: BigText?

  @get:Schema(title = "法人姓名")
  abstract var leaderName: String?

  @get:Schema(title = "类型")
  abstract var type: Int?

  @get:Schema(title = "统一社会信用代码")
  abstract var uniCreditCode: SerialCode?

  @get:Schema(title = "成立日期")
  abstract var createDate: LocalDate?

  @get:Schema(title = "注册资本")
  abstract var regCapital: decimal?

  @get:Schema(title = "营业执照名称")
  abstract var title: String

  @get:Schema(title = "所属上传用户")
  abstract var userId: RefId?
}

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
package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.core.date
import net.yan100.compose.core.decimal
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

@MetaDef
interface SuperBizCert : IJpaEntity {
  /**
   * 所属用户信息 id
   */
  var userInfoId: RefId?

  /**
   * 签发时间
   */
  var issueDate: date?

  /**
   * 地址详情
   */
  var addressDetailsId: RefId?

  /**
   * 注册地 地址代码
   */
  var addressCode: string?

  /**
   * 经营范围
   */
  var bizRange: string?

  /**
   * 法人姓名
   */
  var leaderName: String?

  /**
   * 营业执照类型
   */
  var type: Int?

  /**
   * 统一社会信用代码
   */
  var uniCreditCode: string?

  /**
   * 成立日期
   */
  var createDate: date?

  /**
   * 注册资本
   */
  var regCapital: decimal?

  /**
   * 营业执照名称
   */
  var title: String

  /**
   * 所属上传用户
   */
  var userId: RefId?
}

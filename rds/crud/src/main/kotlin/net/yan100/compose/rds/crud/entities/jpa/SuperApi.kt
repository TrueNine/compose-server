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
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import net.yan100.compose.rds.core.entities.IJpaEntity

/**
 * api
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperApi : IJpaEntity {
  /** ## 权限 id */
  var permissionsId: RefId?

  /** 名称 */
  var name: String?

  /** 描述 */
  var doc: String?

  /** 路径 */
  var apiPath: String?

  /** 请求方式 */
  var apiMethod: String?

  /** 请求协议 */
  var apiProtocol: String?

  @MetaSkipGeneration
  val uriDeep: Int get() = apiPath?.split("/")?.filter { it.isNotBlank() }?.size ?: 0
}

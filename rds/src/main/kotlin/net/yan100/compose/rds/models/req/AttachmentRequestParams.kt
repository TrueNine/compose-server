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
package net.yan100.compose.rds.models.req

import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "记录文件")
class PostAttachmentReq {
  @get:Schema(title = "存储的 uri") var baseUri: String? = null

  @get:Schema(title = "存储的url") var baseUrl: String? = null

  @get:Schema(title = "保存后的名称") var saveName: String? = null
}

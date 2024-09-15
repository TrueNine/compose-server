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
package net.yan100.compose.rds.core.models

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.RefId

@Deprecated("已提上删除议程")
@Schema(title = "表行对象序列化模型")
data class DataRecord(
  var id: RefId? = null,
  var modelHash: Int? = null,
  var lang: String? = null,
  var namespace: String? = null,
  var entityJson: String? = null,
)

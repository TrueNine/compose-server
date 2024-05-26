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
package net.yan100.compose.depend.jvalid.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import net.yan100.compose.depend.jvalid.group.GetGroup
import net.yan100.compose.depend.jvalid.group.PostGroup

@Entity
@Table(name = "table_gets")
class GetEntity {
  @Id @Null(groups = [GetGroup::class], message = "查询时不允许传入 id") @NotNull(groups = [PostGroup::class], message = "新增时必须传入 id") var id: String? = null
  var name: String? = null
  var age: Int? = null
}

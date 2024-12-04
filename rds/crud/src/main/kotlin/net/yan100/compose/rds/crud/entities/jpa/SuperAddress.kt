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

import net.yan100.compose.core.bool
import net.yan100.compose.core.domain.Coordinate
import net.yan100.compose.core.int
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaTreeEntity


@MetaDef
interface SuperAddress : IJpaTreeEntity {
  /** 代码 */
  var code: string

  /** 名称 */
  var name: string

  /** 级别 0 为国家 */
  var level: int?

  /** 年份版本号 */
  var yearVersion: string?

  /** 定位 */
  var center: Coordinate?

  /** 是否为终结地址（如市辖区） */
  var leaf: bool?
}

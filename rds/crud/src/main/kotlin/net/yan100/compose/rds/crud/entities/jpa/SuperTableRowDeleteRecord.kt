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

import jakarta.persistence.Convert
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.crud.converters.RecordModelConverter

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperTableRowDeleteRecord : IJpaEntity {
  /**
   * 表名
   */
  var tableNames: String

  /**
   * 删除用户id
   */
  var userId: RefId?

  /**
   * 删除用户账户
   */
  var userAccount: string?

  /**
   * 删除时间
   */
  var deleteDatetime: datetime

  /**
   * 删除实体
   */
  @get:Convert(converter = RecordModelConverter::class)
  var entity: String?
}

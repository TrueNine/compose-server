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
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.string
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.RecordModelConverter
import net.yan100.compose.rds.core.entities.IEntity

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
@MappedSuperclass
abstract class SuperTableRowDeleteRecord : IEntity() {
  @get:Schema(title = "表名")
  abstract var tableNames: String

  @get:Schema(title = "删除用户id")
  abstract var userId: RefId?

  @get:Schema(title = "删除用户账户")
  abstract var userAccount: string?

  @get:Schema(title = "删除时间")
  abstract var deleteDatetime: datetime

  @get:Convert(converter = RecordModelConverter::class)
  @get:Schema(title = "删除实体")
  abstract var entity: String?
}

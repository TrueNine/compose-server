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
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.alias.*
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaName
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
@MetaName("flyway_schema_history")
abstract class SuperFlywaySchemaHistory : IEntity() {
  @get:Schema(title = "执行是否成功")
  abstract var success: bool?

  @get:Schema(title = "执行时间")
  abstract var executionTime: timestamp?

  @get:Schema(title = "安装时间")
  abstract var installedOn: datetime

  @get:Schema(title = "执行的数据库账号")
  abstract var installedBy: string

  @get:Schema(title = "哈希")
  abstract var checksum: int?

  @get:Schema(title = "执行脚本文件名")
  abstract var script: string

  @get:Schema(title = "类型")
  abstract var type: string?

  @get:Schema(title = "描述")
  abstract var description: string?

  @get:Schema(title = "版本")
  abstract var version: string

  @get:Schema(title = "安装等级")
  abstract var installedRank: int?
}

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
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.*
import net.yan100.compose.rds.Col
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
class SuperFlywaySchemaHistory : IEntity() {
  @Schema(title = "执行是否成功") @Col(name = SUCCESS) var success: bool? = null

  @Schema(title = "执行时间") @Col(name = EXECUTION_TIME) var executionTime: timestamp? = null

  @Schema(title = "安装时间") @Col(name = INSTALLED_ON) lateinit var installedOn: datetime

  @Schema(title = "执行的数据库账号") @Col(name = INSTALLED_BY) lateinit var installedBy: string

  @Schema(title = "哈希") @Col(name = CHECKSUM) var checksum: int? = null

  @Schema(title = "执行脚本文件名") @Col(name = SCRIPT) lateinit var script: string

  @Schema(title = "类型") @Col(name = TYPE) var type: string? = null

  @Schema(title = "描述") @Col(name = DESCRIPTION) var description: string? = null

  @Schema(title = "版本") @Col(name = VERSION) lateinit var version: string

  @Schema(title = "安装等级") @Col(name = INSTALLED_RANK) var installedRank: int? = null

  companion object {
    const val TABLE_NAME = "flyway_schema_history"

    const val INSTALLED_RANK = "installed_rank"
    const val INSTALLED_ON = "installed_on"
    const val INSTALLED_BY = "installed_by"
    const val EXECUTION_TIME = "execution_time"

    const val SUCCESS = "success"

    const val VERSION = "version"

    const val DESCRIPTION = "description"

    const val TYPE = "type"

    const val SCRIPT = "script"

    const val CHECKSUM = "checksum"
  }
}

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperFlywaySchemaHistory.TABLE_NAME)
class FlywaySchemaHistory : SuperFlywaySchemaHistory()

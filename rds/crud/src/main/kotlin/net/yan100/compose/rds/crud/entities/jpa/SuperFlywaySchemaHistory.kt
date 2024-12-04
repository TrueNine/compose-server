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

import net.yan100.compose.core.*
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.rds.core.entities.IJpaEntity

@MetaDef
@MetaName("flyway_schema_history")
interface SuperFlywaySchemaHistory : IJpaEntity {
  /**
   * 执行是否成功
   */
  var success: bool?

  /**
   * 执行时间
   */
  var executionTime: timestamp?

  /**
   * 安装时间
   */
  var installedOn: datetime

  /**
   * 执行的数据库账号
   */
  var installedBy: string

  /**
   * 哈希
   */
  var checksum: int?

  /**
   * 执行脚本文件名
   */
  var script: string

  /**
   * 类型
   */
  var type: string?

  /**
   * 描述
   */
  var description: string?

  /**
   * 版本
   */
  var version: string

  /**
   * 安装等级
   */
  var installedRank: int?
}

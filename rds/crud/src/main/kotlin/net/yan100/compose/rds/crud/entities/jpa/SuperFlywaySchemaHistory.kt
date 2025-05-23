package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.*
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.rds.entities.IJpaEntity

@MetaDef
@MetaName("flyway_schema_history")
interface SuperFlywaySchemaHistory : IJpaEntity {
  /** 执行是否成功 */
  var success: bool?

  /** 执行时间 */
  var executionTime: timestamp?

  /** 安装时间 */
  var installedOn: datetime

  /** 执行的数据库账号 */
  var installedBy: string

  /** 哈希 */
  var checksum: int?

  /** 执行脚本文件名 */
  var script: string

  /** 类型 */
  var type: string?

  /** 描述 */
  var description: string?

  /** 版本 */
  var version: string

  /** 安装等级 */
  var installedRank: int?
}

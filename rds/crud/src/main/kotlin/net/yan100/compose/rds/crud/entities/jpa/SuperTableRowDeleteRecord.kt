package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.RefId
import net.yan100.compose.datetime
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.RecordModelConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.string

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperTableRowDeleteRecord : IJpaEntity {
  /** 表名 */
  var tableNames: String

  /** 删除用户id */
  var userId: RefId?

  /** 删除用户账户 */
  var userAccount: string?

  /** 删除时间 */
  var deleteDatetime: datetime

  /** 删除实体 */
  @get:Convert(converter = RecordModelConverter::class)
  var entity: String?
}

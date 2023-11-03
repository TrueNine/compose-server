package net.yan100.compose.rds.service

import net.yan100.compose.rds.core.entity.BaseEntity
import net.yan100.compose.rds.core.entity.TreeEntity
import net.yan100.compose.rds.entity.TableRowDeleteRecord
import net.yan100.compose.rds.service.base.IService

interface ITableRowDeleteRecordService : IService<TableRowDeleteRecord> {
  fun saveAnyEntity(anyData: BaseEntity?): TableRowDeleteRecord?
}

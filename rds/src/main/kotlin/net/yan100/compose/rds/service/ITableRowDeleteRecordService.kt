package net.yan100.compose.rds.service

import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.core.entities.TreeEntity
import net.yan100.compose.rds.entities.TableRowDeleteRecord
import net.yan100.compose.rds.service.base.IService

interface ITableRowDeleteRecordService : IService<TableRowDeleteRecord> {
  fun saveAnyEntity(anyData: BaseEntity?): TableRowDeleteRecord?
}

package net.yan100.compose.rds.service

import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.entities.TableRowDeleteRecord
import net.yan100.compose.rds.service.base.IService

interface ITableRowDeleteRecordService : IService<TableRowDeleteRecord> {
  fun saveAnyEntity(anyData: IEntity?): TableRowDeleteRecord?
}

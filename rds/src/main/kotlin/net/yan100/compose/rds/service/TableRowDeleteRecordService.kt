package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.TableRowDeleteRecord

interface TableRowDeleteRecordService : BaseService<TableRowDeleteRecord> {
  fun saveAnyEntity(anyData: net.yan100.compose.rds.base.BaseEntity?): TableRowDeleteRecord?
}

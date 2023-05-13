package net.yan100.compose.rds.service

import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.entity.TableRowDeleteRecordEntity

interface TableRowDeleteRecordService : BaseService<TableRowDeleteRecordEntity> {
  fun saveAnyEntity(anyData: net.yan100.compose.rds.base.BaseEntity?): TableRowDeleteRecordEntity?
}

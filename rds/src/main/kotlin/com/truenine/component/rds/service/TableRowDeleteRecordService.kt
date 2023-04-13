package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseEntity
import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.entity.TableRowDeleteRecordEntity

interface TableRowDeleteRecordService : BaseService<TableRowDeleteRecordEntity> {
  fun save(data: BaseEntity?): TableRowDeleteRecordEntity?
}

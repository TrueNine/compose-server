package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseEntity
import com.truenine.component.rds.entity.TableRowDeleteRecordEntity

interface TableRowDeleteRecordService {
  fun save(data: BaseEntity?): TableRowDeleteRecordEntity?
}

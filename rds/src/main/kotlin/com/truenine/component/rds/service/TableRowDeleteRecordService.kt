package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseDao
import com.truenine.component.rds.dao.TableRowDeleteRecordDao

interface TableRowDeleteRecordService {
  fun save(data: BaseDao?): TableRowDeleteRecordDao?
}

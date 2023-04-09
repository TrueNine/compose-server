package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.TableRowDeleteRecordEntity
import org.springframework.stereotype.Repository

@Repository
interface TableRowDeleteRecordRepo : BaseRepo<TableRowDeleteRecordEntity, String> {
}

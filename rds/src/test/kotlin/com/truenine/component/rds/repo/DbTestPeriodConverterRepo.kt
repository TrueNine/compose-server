package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.DbTestPeriodConverterEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestPeriodConverterRepo : BaseRepo<DbTestPeriodConverterEntity, String> {
}

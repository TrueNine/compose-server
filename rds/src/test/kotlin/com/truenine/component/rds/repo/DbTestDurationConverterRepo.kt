package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.DbTestDurationConverterEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestDurationConverterRepo : BaseRepo<DbTestDurationConverterEntity, String> {
}

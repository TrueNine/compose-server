package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.DbTestBaseServiceEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestBaseServiceRepository : BaseRepo<DbTestBaseServiceEntity> {
}

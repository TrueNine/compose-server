package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.DbTestBaseServiceEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestBaseServiceRepository : BaseRepository<DbTestBaseServiceEntity> {
}

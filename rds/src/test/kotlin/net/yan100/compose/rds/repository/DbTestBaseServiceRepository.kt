package net.yan100.compose.rds.repository


import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.DbTestBaseServiceEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestBaseServiceRepository : BaseRepository<DbTestBaseServiceEntity>

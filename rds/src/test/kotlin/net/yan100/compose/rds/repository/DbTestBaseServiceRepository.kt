package net.yan100.compose.rds.repository


import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.DbTestServiceEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestBaseServiceRepository : BaseRepository<DbTestServiceEntity>

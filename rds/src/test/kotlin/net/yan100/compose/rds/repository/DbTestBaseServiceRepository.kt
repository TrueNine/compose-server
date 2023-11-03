package net.yan100.compose.rds.repository


import net.yan100.compose.rds.entity.DbTestServiceEntity
import net.yan100.compose.rds.repository.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface DbTestBaseServiceRepository : IRepo<DbTestServiceEntity>

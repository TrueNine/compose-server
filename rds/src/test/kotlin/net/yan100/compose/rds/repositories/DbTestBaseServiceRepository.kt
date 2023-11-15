package net.yan100.compose.rds.repositories


import net.yan100.compose.rds.entities.DbTestServiceEntity
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface DbTestBaseServiceRepository : IRepo<DbTestServiceEntity>

package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.DbTestDurationConverterEntity
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface DbTestDurationConverterRepository :
    IRepo<DbTestDurationConverterEntity>

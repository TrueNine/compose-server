package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.DbTestAllLateEntity
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IDbTestAllLateEntityRepo : IRepo<DbTestAllLateEntity> {
}

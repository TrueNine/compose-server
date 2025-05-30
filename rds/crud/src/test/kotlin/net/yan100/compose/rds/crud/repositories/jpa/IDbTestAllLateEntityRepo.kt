package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.DbTestAllLateEntity
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IDbTestAllLateEntityRepo : IRepo<DbTestAllLateEntity>

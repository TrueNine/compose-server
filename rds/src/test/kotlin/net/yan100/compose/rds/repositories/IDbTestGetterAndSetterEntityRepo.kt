package net.yan100.compose.rds.repositories

import net.yan100.compose.core.i32
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.DbTestGetterAndSetterEntity
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IDbTestGetterAndSetterEntityRepo : IRepo<DbTestGetterAndSetterEntity> {
  fun existsByName(name: String): Boolean
  fun existsByAged(aged: i32): Boolean
}

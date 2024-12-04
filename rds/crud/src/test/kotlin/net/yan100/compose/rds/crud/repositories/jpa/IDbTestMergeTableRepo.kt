package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.i32
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.DbTestMergeTable
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IDbTestMergeTableRepo : IRepo<DbTestMergeTable> {
  fun findAllByName(name: string): List<DbTestMergeTable>
  fun findAllByAge(age: i32): List<DbTestMergeTable>
}

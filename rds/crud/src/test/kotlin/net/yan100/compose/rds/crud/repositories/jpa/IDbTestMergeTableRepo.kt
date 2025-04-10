package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.i32
import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.DbTestMergeTable
import net.yan100.compose.string
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IDbTestMergeTableRepo : IRepo<DbTestMergeTable> {
  fun findAllByName(name: string): List<DbTestMergeTable>

  fun findAllByAge(age: i32): List<DbTestMergeTable>
}

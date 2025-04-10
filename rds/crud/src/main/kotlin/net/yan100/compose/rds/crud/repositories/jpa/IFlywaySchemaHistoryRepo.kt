package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.jpa.FlywaySchemaHistory
import net.yan100.compose.string
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("IFlywaySchemaHistoryRepository")
interface IFlywaySchemaHistoryRepo : IRepo<FlywaySchemaHistory> {
  @Modifying
  @Query("drop table if exists :name", nativeQuery = true)
  fun nativeDropTableForPostgresql(
    name: string = FlywaySchemaHistory.TABLE_NAME
  )

  @Modifying
  @Query("drop table if exists :name", nativeQuery = true)
  fun nativeDropTableForMysql(name: string = FlywaySchemaHistory.TABLE_NAME)
}

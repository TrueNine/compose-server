package net.yan100.compose.rds.repositories

import net.yan100.compose.core.alias.string
import net.yan100.compose.rds.entities.FlywaySchemaHistory
import net.yan100.compose.rds.entities.SuperFlywaySchemaHistory
import net.yan100.compose.rds.repositories.base.IRepo
import org.jetbrains.annotations.ApiStatus
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IFlywaySchemaHistoryRepo : IRepo<FlywaySchemaHistory> {
    @Query("drop table if exists :name", nativeQuery = true)
    @Modifying
    fun nativeDropTableForPostgresql(name: string = SuperFlywaySchemaHistory.TABLE_NAME)

    @ApiStatus.Experimental
    @Query("drop table if exists :name", nativeQuery = true)
    @Modifying
    fun nativeDropTableForMysql(name: string = SuperFlywaySchemaHistory.TABLE_NAME)


}

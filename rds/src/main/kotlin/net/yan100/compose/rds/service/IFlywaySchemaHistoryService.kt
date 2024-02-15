package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.FlywaySchemaHistory
import net.yan100.compose.rds.service.base.IService


interface IFlywaySchemaHistoryService : IService<FlywaySchemaHistory> {
    fun clean()
}

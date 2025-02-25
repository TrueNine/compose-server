package net.yan100.compose.rds.crud.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.FlywaySchemaHistory

interface IFlywaySchemaHistoryService : ICrud<FlywaySchemaHistory> {
  fun clean()
}

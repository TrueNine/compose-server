package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.TableRowDeleteRecord
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("ITableRowDeleteRecordRepository")
interface ITableRowDeleteRecordRepo : IRepo<TableRowDeleteRecord>

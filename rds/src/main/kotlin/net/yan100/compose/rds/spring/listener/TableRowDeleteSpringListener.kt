package net.yan100.compose.rds.spring.listener


import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.event.TableRowDeleteSpringEvent
import net.yan100.compose.rds.service.ITableRowDeleteRecordService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class TableRowDeleteSpringListener(
    private val tableRowDeleteRecordService: ITableRowDeleteRecordService
) : ApplicationListener<TableRowDeleteSpringEvent> {
    override fun onApplicationEvent(event: TableRowDeleteSpringEvent) {
        tableRowDeleteRecordService.saveAnyEntity(event.source as IEntity)
    }
}

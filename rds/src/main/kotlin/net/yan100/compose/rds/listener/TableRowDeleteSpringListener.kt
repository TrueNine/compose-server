package net.yan100.compose.rds.listener


import net.yan100.compose.rds.event.TableRowDeleteSpringEvent
import net.yan100.compose.rds.service.TableRowDeleteRecordService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class TableRowDeleteSpringListener(
  private val tableRowDeleteRecordService: TableRowDeleteRecordService
) : ApplicationListener<TableRowDeleteSpringEvent> {
  override fun onApplicationEvent(event: TableRowDeleteSpringEvent) {
    tableRowDeleteRecordService.saveAnyEntity(event.source as net.yan100.compose.rds.base.BaseEntity)
  }
}

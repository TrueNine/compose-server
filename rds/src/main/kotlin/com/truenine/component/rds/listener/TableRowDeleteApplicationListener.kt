package com.truenine.component.rds.listener

import com.truenine.component.rds.base.BaseDao
import com.truenine.component.rds.event.TableRowDeleteSpringEvent
import com.truenine.component.rds.service.TableRowDeleteRecordService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
open class TableRowDeleteApplicationListener(
  private val tableRowDeleteRecordService: TableRowDeleteRecordService
) : ApplicationListener<TableRowDeleteSpringEvent> {
  override fun onApplicationEvent(event: TableRowDeleteSpringEvent) {
    tableRowDeleteRecordService.save(event.source as BaseDao)
  }
}

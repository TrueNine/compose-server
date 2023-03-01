package com.truenine.component.rds.event

import com.truenine.component.rds.service.BackupService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
open class DelListener(
  private val backupService: BackupService
) : ApplicationListener<DelEvent> {
  override fun onApplicationEvent(event: DelEvent) {
    backupService.save(event.source)
  }
}

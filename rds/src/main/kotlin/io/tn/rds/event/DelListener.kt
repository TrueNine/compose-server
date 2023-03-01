package io.tn.rds.event

import io.tn.rds.service.BackupService
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

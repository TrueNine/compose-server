package com.truenine.component.rds.listener

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.event.DelEvent
import jakarta.persistence.PreRemove
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * 备份删除监听器
 *
 * @author TrueNine
 * @since 2022-12-13
 */
@Slf4j
@Component
class DeleteBackupListener {
  private var pub: ApplicationEventPublisher? = null

  init {
    log.info("注册数据删除监听器 = {}", this.javaClass)
  }

  @Autowired
  fun setPub(pub: ApplicationEventPublisher?) {
    this.pub = pub
  }

  @PreRemove
  fun a(obj: Any?) {
    if (null != obj) {
      pub!!.publishEvent(DelEvent(obj))
    }
  }

  companion object {
    private val log = LogKt.getLog(DeleteBackupListener::class)
  }
}

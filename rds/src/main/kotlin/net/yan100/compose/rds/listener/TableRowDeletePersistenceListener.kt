package net.yan100.compose.rds.listener

import jakarta.persistence.PreRemove
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.event.TableRowDeleteSpringEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * 备份删除监听器
 *
 * @author TrueNine
 * @since 2022-12-13
 */
@Component
class TableRowDeletePersistenceListener {
  private lateinit var pub: ApplicationEventPublisher

  init {
    log.debug("注册jpa数据删除监听器 = {}", this)
  }

  @Autowired
  fun setPub(pub: ApplicationEventPublisher) {
    this.pub = pub
  }

  @PreRemove
  fun preRemoveHandle(models: Any?) {
    log.debug("进行数据删除 models = {}", models)
    if (null != models) {
      log.debug("发布删除事件 models = {}", models)
      pub.publishEvent(TableRowDeleteSpringEvent(models))
    }
  }

  companion object {
    private val log = slf4j(TableRowDeletePersistenceListener::class)
  }
}

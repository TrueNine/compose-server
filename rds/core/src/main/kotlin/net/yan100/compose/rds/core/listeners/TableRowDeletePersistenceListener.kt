package net.yan100.compose.rds.core.listeners

import jakarta.annotation.Resource
import jakarta.persistence.PreRemove
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.event.TableRowDeleteSpringEvent
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
  lateinit var pub: ApplicationEventPublisher
    @Resource set

  init {
    log.debug("注册jpa数据删除监听器 = {}", this)
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

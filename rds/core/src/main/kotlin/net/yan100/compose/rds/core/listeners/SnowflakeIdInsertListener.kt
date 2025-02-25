package net.yan100.compose.rds.core.listeners

import jakarta.annotation.Resource
import jakarta.persistence.PrePersist
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.entities.IJpaPersistentEntity
import org.springframework.stereotype.Component

private val log = slf4j<SnowflakeIdInsertListener>()

@Component
class SnowflakeIdInsertListener {
  private lateinit var internalSnowflake: ISnowflakeGenerator
  var snowflake: ISnowflakeGenerator
    @Resource
    set(v) {
      log.trace("注册 id 生成器监听器: {}", v)
      internalSnowflake = v
    }
    get() = internalSnowflake

  @PrePersist
  fun insertId(data: Any?) {
    if (data is IJpaPersistentEntity && data.isNew) {
      data.id = snowflake.next()
    }
  }
}

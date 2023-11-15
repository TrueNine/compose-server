package net.yan100.compose.rds.core.listener

import jakarta.persistence.PrePersist
import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.rds.core.entities.AnyEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SnowflakeIdInsertListener {
  @set:Autowired
  lateinit var snowflake: Snowflake

  @PrePersist
  fun insertId(data: Any?) {
    if (data is AnyEntity) {
      data.id = snowflake.nextStringId()
    }
  }
}

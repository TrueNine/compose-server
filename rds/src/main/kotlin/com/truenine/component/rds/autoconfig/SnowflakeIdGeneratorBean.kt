package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.Snowflake
import com.truenine.component.core.lang.LogKt
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
class SnowflakeIdGeneratorBean(
  private val snowflake: Snowflake
) : IdentifierGenerator {
  private val log = LogKt.getLog(this::class)

  init {
    log.trace("注册 id 生成器 workId = {}", snowflake)
  }

  companion object {
    const val NAME = "customerSimpleSnowflakeIdGenerator"
    const val CLASS_NAME = "com.truenine.component.rds.autoconfig.SnowflakeIdGeneratorBean"
  }

  override fun generate(
    session: SharedSessionContractImplementor?,
    obj: Any?
  ): Any {
    val snowflakeId = snowflake.nextStringId()
    log.trace("当前生成的 snowflakeId = {}", snowflakeId)
    return snowflakeId
  }
}

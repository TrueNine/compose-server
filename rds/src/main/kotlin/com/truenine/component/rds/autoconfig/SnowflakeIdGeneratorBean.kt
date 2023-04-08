package com.truenine.component.rds.autoconfig

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.util.SnowflakeGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
open class SnowflakeIdGeneratorBean : IdentifierGenerator {
  private val log = LogKt.getLog(this::class)

  init {
    log.debug("注册 id 生成器")
  }

  companion object {
    const val NAME = "customerSimpleSnowflakeIdGenerator"
    const val CLASS_NAME =
      "com.truenine.component.rds.autoconfig.SnowflakeIdGeneratorBean"
  }

  override fun generate(
    session: SharedSessionContractImplementor?,
    obj: Any?
  ): Any {
    val snowflakeId = SnowflakeGenerator.nextStr()
    log.debug("当前生成的 snowflake = {}", snowflakeId)
    return snowflakeId
  }
}

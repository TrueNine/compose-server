package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.Snowflake
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
open class SnowflakeIdGeneratorBean(
  private val snowflake: Snowflake
) : IdentifierGenerator {

  companion object {
    const val NAME_SPACE = "com.truenine.component.rds.autoconfig.SnowflakeIdGeneratorBean"
    const val NAME = "customerSimpleSnowflakeIdGenerator"
  }

  override fun generate(
    session: SharedSessionContractImplementor?,
    `object`: Any?
  ): Any {
    return snowflake.nextIdStr()
  }
}

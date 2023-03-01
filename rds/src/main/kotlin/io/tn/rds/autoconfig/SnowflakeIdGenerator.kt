package io.tn.rds.autoconfig

import io.tn.core.id.Snowflake
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class SnowflakeIdGenerator @Autowired constructor(
  private val snowflake: Snowflake
) : IdentifierGenerator {

  override fun generate(
    session: SharedSessionContractImplementor?,
    `object`: Any?
  ): Any {
    return snowflake.nextIdStr()
  }
}

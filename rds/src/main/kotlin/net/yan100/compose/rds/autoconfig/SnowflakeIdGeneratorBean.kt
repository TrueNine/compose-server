package net.yan100.compose.rds.autoconfig


import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.core.lang.slf4j
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
class SnowflakeIdGeneratorBean(
  private val snowflake: Snowflake
) : IdentifierGenerator {
  private val log = slf4j(this::class)

  init {
    log.trace("注册 id 生成器 workId = {}", snowflake)
  }

  companion object {
    const val CLASS_NAME = "net.yan100.compose.rds.autoconfig.SnowflakeIdGeneratorBean"
    const val NAME = "customerSimpleSnowflakeIdGenerator"
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

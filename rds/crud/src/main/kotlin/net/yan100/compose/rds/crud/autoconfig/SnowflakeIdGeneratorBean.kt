package net.yan100.compose.rds.crud.autoconfig

import jakarta.annotation.Resource
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.core.slf4j
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

private val log = slf4j<SnowflakeIdGeneratorBean>()

@Component
class SnowflakeIdGeneratorBean : IdentifierGenerator {

  lateinit var snowflake: ISnowflakeGenerator
    @Resource set

  init {
    log.trace("注册 id 生成器 当前未初始")
  }

  companion object {
    const val CLASS_NAME =
      "net.yan100.compose.rds.autoconfig.SnowflakeIdGeneratorBean"
    const val NAME = "customerSimpleSnowflakeIdGenerator"
  }

  override fun generate(
    session: SharedSessionContractImplementor?,
    obj: Any?,
  ): Any {
    val snowflakeId = snowflake.nextString()
    log.trace("当前生成的 snowflakeId = {}", snowflakeId)
    return snowflakeId
  }
}

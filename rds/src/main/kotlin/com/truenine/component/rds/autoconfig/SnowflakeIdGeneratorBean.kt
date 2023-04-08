package com.truenine.component.rds.autoconfig

import cn.hutool.core.lang.Snowflake
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.util.SnowflakeGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.Configurable
import org.hibernate.id.IdentifierGenerator
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.Type
import java.util.*

open class SnowflakeIdGeneratorBean : IdentifierGenerator, Configurable {
  private val log = LogKt.getLog(this::class)

  init {
    log.debug("注册id 生成器")
  }

  private var workId: Long? = null
  private var datacenterId: Long? = null
  private var timeStamp: Long? = null
  override fun configure(
    type: Type?,
    parameters: Properties?,
    serviceRegistry: ServiceRegistry?
  ) {
    workId = parameters?.get("workId")?.toString()?.toLongOrNull()
    datacenterId = parameters?.get("datacenterId")?.toString()?.toLongOrNull()
    timeStamp = parameters?.get("timeStamp")?.toString()?.toLongOrNull()
  }

  companion object {
    const val NAME = "customerSimpleSnowflakeIdGenerator"
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

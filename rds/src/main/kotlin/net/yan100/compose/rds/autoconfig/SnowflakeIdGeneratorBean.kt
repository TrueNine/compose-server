package net.yan100.compose.rds.autoconfig


import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.core.lang.slf4j
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SnowflakeIdGeneratorBean : IdentifierGenerator {
    private val log = slf4j(this::class)

    @Autowired
    private lateinit var snowflake: Snowflake

    init {
        log.trace("注册 id 生成器 当前未初始")
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

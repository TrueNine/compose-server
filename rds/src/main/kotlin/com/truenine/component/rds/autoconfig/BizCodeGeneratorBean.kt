package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.Snowflake
import com.truenine.component.core.lang.LogKt
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class BizCodeGeneratorBean(
  private val snowflake: Snowflake
) : IdentifierGenerator {
  init {
    log.debug("注册业务单号生成器")
  }

  companion object {
    private val log = LogKt.getLog(this::class)
    const val CLASS_NAME = "com.truenine.component.rds.autoconfig.BizCodeGeneratorBean"
    const val NAME = "BizCodeGeneratorBeanBitCast"
  }

  override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): String {
    val dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    val st = snowflake.nextStr()
    val code = "$dt${st.substring(st.length - 4)}"
    log.trace("生成的单号为 {}", code)
    return code
  }
}

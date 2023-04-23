package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.BizCode
import com.truenine.component.core.lang.LogKt
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
class BizCodeGeneratorBean(
  private val bizCode: BizCode
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
    val c = bizCode.nextCodeStr()
    log.trace("生成的业务单号 = {}", c)
    return c
  }
}

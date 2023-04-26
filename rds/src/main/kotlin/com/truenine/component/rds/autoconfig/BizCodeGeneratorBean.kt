package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.BizCodeGenerator
import com.truenine.component.core.lang.slf4j
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
class BizCodeGeneratorBean(
  private val bizCodeGenerator: BizCodeGenerator
) : IdentifierGenerator {
  init {
    log.debug("注册业务单号生成器")
  }

  companion object {
    private val log = slf4j(this::class)
    const val CLASS_NAME = "com.truenine.component.rds.autoconfig.BizCodeGeneratorBean"
    const val NAME = "BizCodeGeneratorBeanBitCasts"
  }

  override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): Any {
    val c = bizCodeGenerator.nextCodeStr()
    return c
  }
}

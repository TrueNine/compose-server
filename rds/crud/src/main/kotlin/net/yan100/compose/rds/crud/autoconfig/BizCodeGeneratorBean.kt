package net.yan100.compose.rds.crud.autoconfig

import net.yan100.compose.generator.IOrderCodeGenerator
import net.yan100.compose.slf4j
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Component

@Component
class BizCodeGeneratorBean(private val bizCodeGenerator: IOrderCodeGenerator) :
  IdentifierGenerator {
  init {
    log.debug("注册业务单号生成器")
  }

  companion object {
    private val log = slf4j(this::class)
    const val CLASS_NAME =
      "net.yan100.compose.rds.autoconfig.BizCodeGeneratorBean"
    const val NAME = "BizCodeGeneratorBeanBitCasts"
  }

  override fun generate(
    session: SharedSessionContractImplementor?,
    `object`: Any?,
  ): Any {
    val c = bizCodeGenerator.nextString()
    return c
  }
}

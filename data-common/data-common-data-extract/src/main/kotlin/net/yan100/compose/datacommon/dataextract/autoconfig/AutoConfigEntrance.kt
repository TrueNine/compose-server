package net.yan100.compose.datacommon.dataextract.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(
  "com.truenine.component.datacommon.dataextract.service"
)
@Import(
  ApiExchangesAutoConfiguration::class
)
class AutoConfigEntrance

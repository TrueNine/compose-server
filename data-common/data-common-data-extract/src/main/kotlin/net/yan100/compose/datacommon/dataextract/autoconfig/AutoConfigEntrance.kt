package net.yan100.compose.datacommon.dataextract.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(
  "net.yan100.compose.datacommon.dataextract.service"
)
@Import(
  ApiExchangesAutoConfiguration::class
)
class AutoConfigEntrance

package com.truenine.component.datacommon.dataextract.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(
  "com.truenine.component.datacommon.dataextract.service"
)
@Import(
  RemoteCallsAutoConfiguration::class
)
class AutoConfigEntrance {
}

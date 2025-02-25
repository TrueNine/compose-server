package net.yan100.compose.data.extract.autoconfig

import org.springframework.context.annotation.ComponentScan

@ComponentScan(
  "net.yan100.compose.data.extract.service",
  "net.yan100.compose.data.extract.autoconfig",
)
class AutoConfigEntrance

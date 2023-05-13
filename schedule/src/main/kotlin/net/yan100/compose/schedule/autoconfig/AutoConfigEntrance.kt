package net.yan100.compose.schedule.autoconfig

import net.yan100.compose.schedule.properties.XxlJobAutoConfigurationProperties
import net.yan100.compose.schedule.properties.XxlJobExecutorAutoConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(
  value = [
    XxlJobAutoConfigurationProperties::class,
    XxlJobExecutorAutoConfigurationProperties::class
  ]
)
class AutoConfigEntrance

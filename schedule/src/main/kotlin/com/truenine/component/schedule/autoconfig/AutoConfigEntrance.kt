package com.truenine.component.schedule.autoconfig

import com.truenine.component.schedule.properties.XxlJobAutoConfigurationProperties
import com.truenine.component.schedule.properties.XxlJobExecutorAutoConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(
  value = [
    XxlJobAutoConfigurationProperties::class,
    XxlJobExecutorAutoConfigurationProperties::class
  ]
)
class AutoConfigEntrance

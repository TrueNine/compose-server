package com.truenine.component.cacheable.autoconfig

import org.springframework.context.annotation.Import

@Import(
  RedisJsonSerializerAutoConfiguration::class
)
class AutoConfigEntrance

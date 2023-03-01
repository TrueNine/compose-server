package com.truenine.component.cacheable.autoconfig

import org.springframework.context.annotation.Import

@Import(
  RedisJsonConfig::class
)
class AutoConfigEntrance {
}

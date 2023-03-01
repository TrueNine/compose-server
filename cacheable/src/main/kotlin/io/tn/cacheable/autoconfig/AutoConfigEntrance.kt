package io.tn.cacheable.autoconfig

import org.springframework.context.annotation.Import

@Import(
  RedisJsonConfig::class
)
class AutoConfigEntrance {
}
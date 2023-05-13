package net.yan100.compose.cacheable.autoconfig

import org.springframework.context.annotation.Import

@Import(
  RedisJsonSerializerAutoConfiguration::class
)
class AutoConfigEntrance

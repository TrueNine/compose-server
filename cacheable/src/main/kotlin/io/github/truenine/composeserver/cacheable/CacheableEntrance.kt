package io.github.truenine.composeserver.cacheable

import io.github.truenine.composeserver.cacheable.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class) @SpringBootApplication internal class CacheableEntrance

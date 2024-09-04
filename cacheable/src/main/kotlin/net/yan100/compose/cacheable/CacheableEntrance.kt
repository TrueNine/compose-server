package net.yan100.compose.cacheable

import net.yan100.compose.cacheable.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class)
@SpringBootApplication
internal class CacheableEntrance

internal fun main(args: Array<String>) {
  runApplication<CacheableEntrance>(*args)
}

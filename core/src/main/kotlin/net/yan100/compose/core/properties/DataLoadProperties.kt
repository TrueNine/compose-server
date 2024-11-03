package net.yan100.compose.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.core"

@ConfigurationProperties(prefix = "$PREFIX.data-load")
data class DataLoadProperties(
  val location: String = "data",
  val configLocation: String = "config"
)

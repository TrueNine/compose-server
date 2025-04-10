package net.yan100.compose.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.core"

@ConfigurationProperties(prefix = "$PREFIX.data-load")
data class DataLoadProperties(
  val location: String = "data",
  val matchFiles: MutableList<String> = mutableListOf(),
  val configLocation: String = "config",
)

package io.github.truenine.composeserver.autoconfig

import io.github.truenine.composeserver.properties.DataLoadProperties
import io.github.truenine.composeserver.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(SnowflakeProperties::class, DataLoadProperties::class)
@ComponentScan("io.github.truenine.composeserver.autoconfig")
class AutoConfigEntrance

package net.yan100.compose.autoconfig

import net.yan100.compose.properties.DataLoadProperties
import net.yan100.compose.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(SnowflakeProperties::class, DataLoadProperties::class) @ComponentScan("net.yan100.compose.autoconfig") class AutoConfigEntrance

package net.yan100.compose.depend.springdocopenapi.autoconfig

import net.yan100.compose.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan("net.yan100.compose.depend.springdocopenapi.autoconfig")
@EnableConfigurationProperties(SpringdocOpenApiProperties::class)
class AutoConfigEntrance

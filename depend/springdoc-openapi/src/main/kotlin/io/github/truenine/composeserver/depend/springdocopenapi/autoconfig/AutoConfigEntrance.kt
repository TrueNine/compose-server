package io.github.truenine.composeserver.depend.springdocopenapi.autoconfig

import io.github.truenine.composeserver.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan("io.github.truenine.composeserver.depend.springdocopenapi.autoconfig")
@EnableConfigurationProperties(SpringdocOpenApiProperties::class)
class AutoConfigEntrance

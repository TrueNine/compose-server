package io.github.truenine.composeserver.security.oauth2

import io.github.truenine.composeserver.security.oauth2.autoconfig.WxpaAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication @Import(WxpaAutoConfiguration::class) class SecurityOauth2Entrance

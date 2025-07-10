package net.yan100.compose.security.oauth2

import net.yan100.compose.security.oauth2.autoconfig.WxpaAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication @Import(WxpaAutoConfiguration::class) internal class SecurityOauth2Entrance

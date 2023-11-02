package net.yan100.compose.security.autoconfig

import net.yan100.compose.security.properties.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * 自动配置入口
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@EnableConfigurationProperties(JwtProperties::class)
@ComponentScan("net.yan100.compose.security.autoconfig")
class AutoConfigEntrance

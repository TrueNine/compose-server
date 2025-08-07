package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.security.properties.JwtProperties
import io.github.truenine.composeserver.security.properties.KeysProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * 自动配置入口
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@EnableConfigurationProperties(JwtProperties::class, KeysProperties::class)
@ComponentScan("io.github.truenine.composeserver.security.autoconfig")
class AutoConfigEntrance

package io.github.truenine.composeserver.security.annotations

import io.github.truenine.composeserver.security.autoconfig.JwtIssuerAutoConfiguration
import java.lang.annotation.Inherited
import org.springframework.context.annotation.Import

/**
 * 启动 jwt 验证客户端
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Import(JwtIssuerAutoConfiguration::class)
@Inherited
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableJwtIssuer

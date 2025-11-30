package io.github.truenine.composeserver.security.annotations

import io.github.truenine.composeserver.security.autoconfig.JwtIssuerAutoConfiguration
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * Enable JWT issuer client auto-configuration.
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

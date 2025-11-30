package io.github.truenine.composeserver.security.annotations

import io.github.truenine.composeserver.security.autoconfig.JwtVerifierAutoConfiguration
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * Enable JWT verifier client auto-configuration.
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Import(JwtVerifierAutoConfiguration::class)
@Inherited
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableJwtVerifier

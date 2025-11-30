package io.github.truenine.composeserver.security.annotations

import io.github.truenine.composeserver.security.autoconfig.SecurityPolicyBean
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * Enable REST security manager configuration.
 *
 * @author TrueNine
 * @since 2022-09-29
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(SecurityPolicyBean::class)
annotation class EnableRestSecurity(
  /** @return Request patterns to be permitted without authentication */
  val allowPatterns: Array<String> = [],
  /** @return Login URL patterns */
  val loginUrl: Array<String> = [],
  /** @return Logout URL patterns */
  val logoutUrl: Array<String> = [],
  /** @return Whether to allow Swagger API endpoints */
  val allowSwagger: Boolean = false,
  /** @return Whether to allow webjars resources */
  val allowWebJars: Boolean = true,
  /** @return Whether any request requires authentication */
  val anyRequestAuthed: Boolean = false,
)

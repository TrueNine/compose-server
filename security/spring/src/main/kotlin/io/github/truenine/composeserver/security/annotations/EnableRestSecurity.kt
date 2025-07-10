package io.github.truenine.composeserver.security.annotations

import io.github.truenine.composeserver.security.autoconfig.SecurityPolicyBean
import java.lang.annotation.Inherited
import org.springframework.context.annotation.Import

/**
 * 开启安全管理器
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
  /** @return 需要放行的匹配规则 */
  val allowPatterns: Array<String> = [],
  /** @return 用户登录 url */
  val loginUrl: Array<String> = [],
  /** @return 退出登录 url */
  val logoutUrl: Array<String> = [],
  /** @return 允许 swagger api 放行 */
  val allowSwagger: Boolean = false,
  /** @return 允许 放行webjars */
  val allowWebJars: Boolean = true,
  /** @return 任意地请求是否需要认证 */
  val anyRequestAuthed: Boolean = false,
)

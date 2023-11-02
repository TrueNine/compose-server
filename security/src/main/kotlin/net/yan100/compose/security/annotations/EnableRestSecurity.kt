package net.yan100.compose.security.annotations

import net.yan100.compose.security.autoconfig.SecurityPolicyBean
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

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
  /**
   * @return 需要放行的匹配规则
   */
  val allowPatterns: Array<String> = [],
  /**
   * @return 用户登录 url
   */
  val loginUrl: Array<String> = [],
  /**
   * @return 退出登录 url
   */
  val logoutUrl: Array<String> = [],
  /**
   * @return 允许 swagger api 放行
   */
  val allowSwagger: Boolean = false,
  /**
   * @return 允许 放行webjars
   */
  val allowWebJars: Boolean = true,
  /**
   * @return 任意地请求是否需要认证
   */
  val anyRequestAuthed: Boolean = false
)

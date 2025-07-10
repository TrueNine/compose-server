package net.yan100.compose.security

import net.yan100.compose.security.spring.security.SecurityExceptionAdware
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter
import net.yan100.compose.security.spring.security.SecurityUserDetailsService
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.web.access.intercept.RequestAuthorizationContext

/**
 * spring security 安全策略配置
 *
 * @author TrueNine
 * @since 2022-12-10
 */
class SecurityPolicyDefine {
  var preValidFilter: SecurityPreflightValidFilter? = null
  var service: SecurityUserDetailsService? = null
  var exceptionAdware: SecurityExceptionAdware? = null
  var anonymousPatterns: MutableList<String> = ArrayList()
  var swaggerPatterns: MutableList<String> = ArrayList(listOf("/v3/api-docs/**", "/v3/api-docs.yaml", "/doc.html**", "/swagger-ui/**"))
  var accessor: AuthorizationManager<RequestAuthorizationContext>? = null
}

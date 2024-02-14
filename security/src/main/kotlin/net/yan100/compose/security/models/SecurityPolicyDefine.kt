package net.yan100.compose.security.models

import net.yan100.compose.security.spring.security.SecurityExceptionAdware
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter
import net.yan100.compose.security.spring.security.SecurityUserDetailsService


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
    var swaggerPatterns: MutableList<String> = ArrayList(
        listOf(
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/doc.html**",
            "/swagger-ui/**"
        )
    )
}

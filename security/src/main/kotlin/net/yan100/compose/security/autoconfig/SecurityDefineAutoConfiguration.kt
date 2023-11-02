package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.security.defaults.EmptyPreflightValidFilter
import net.yan100.compose.security.defaults.EmptySecurityDetailsService
import net.yan100.compose.security.defaults.EmptySecurityExceptionAdware
import net.yan100.compose.security.models.SecurityPolicyDefine
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnMissingBean(SecurityPolicyDefine::class)
class SecurityDefineAutoConfiguration {

  private val log = slf4j(this::class)

  @Bean
  @ConditionalOnMissingBean(SecurityPolicyDefine::class)
  fun securityPolicyDefine(mapper: ObjectMapper): SecurityPolicyDefine {
    var se = SecurityPolicyDefine()
    se = checkPolicy(se, mapper)
    log.warn("警告：正在使用默认的测试安全定义 $se ,生产环境请替换")
    se.anonymousPatterns += "/**"
    return se
  }

  private fun checkPolicy(
    desc: SecurityPolicyDefine,
    mapper: ObjectMapper
  ): SecurityPolicyDefine {
    if (desc.exceptionAdware == null) {
      log.debug("正在使用空体异常处理器")
      desc.exceptionAdware = EmptySecurityExceptionAdware(mapper)
    }
    if (desc.service == null) {
      log.debug("正在使用空体 UserDetails")
      desc.service = EmptySecurityDetailsService()
    }
    if (desc.preValidFilter == null) {
      log.debug("正在使用空体 安全过滤器")
      desc.preValidFilter = EmptyPreflightValidFilter()
    }
    return desc
  }
}

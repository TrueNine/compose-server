package com.truenine.component.security.autoconfig

import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.defaults.EmptyPreValidFilter
import com.truenine.component.security.defaults.EmptySecurityDetailsService
import com.truenine.component.security.defaults.EmptySecurityExceptionAdware
import com.truenine.component.security.models.SecurityPolicyDefineModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SecurityDefineAutoConfiguration {

  private val log = LogKt.getLog(this::class)

  @Bean
  open fun securityPolicyDefineModel(): SecurityPolicyDefineModel {
    var se = SecurityPolicyDefineModel()
    se = checkPolicy(se)
    log.warn("警告：正在使用默认的测试安全定义 $se ,生产环境请替换")
    se.anonymousPatterns.add("/**")
    return se
  }

  private fun checkPolicy(desc: SecurityPolicyDefineModel): SecurityPolicyDefineModel {
    if (desc.exceptionAdware == null) {
      log.debug("正在使用空体异常处理器")
      desc.exceptionAdware = EmptySecurityExceptionAdware()
    }
    if (desc.service == null) {
      log.debug("正在使用空体 UserDetails")
      desc.service = EmptySecurityDetailsService()
    }
    if (desc.preValidFilter == null) {
      log.debug("正在使用空体 安全过滤器")
      desc.preValidFilter = EmptyPreValidFilter()
    }
    return desc
  }
}

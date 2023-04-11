package com.truenine.component.security.autoconfig

import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.models.SecurityPolicyDefineModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SecurityDefineAutoConfiguration {

  private val log = LogKt.getLog(this::class)

  @Bean
  open fun securityPolicyDefineModel(): SecurityPolicyDefineModel {
    val se = SecurityPolicyDefineModel()
    log.warn("警告：正在使用默认的测试安全定义 $se ,生产环境请替换")
    se.anonymousPatterns.add("/**")
    return se
  }
}

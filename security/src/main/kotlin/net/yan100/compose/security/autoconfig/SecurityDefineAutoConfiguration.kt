/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
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

  private fun checkPolicy(desc: SecurityPolicyDefine, mapper: ObjectMapper): SecurityPolicyDefine {
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

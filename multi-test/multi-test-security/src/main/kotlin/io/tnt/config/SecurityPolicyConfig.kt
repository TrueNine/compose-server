package io.tnt.config

import com.daojiatech.center.config.PreValidFilter
import com.daojiatech.center.config.SecurityServiceConfig
import com.daojiatech.center.security.ExceptionAdware
import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.rds.service.UserService
import com.truenine.component.rds.service.aggregator.RbacAggregator
import com.truenine.component.security.jwt.JwtIssuer
import com.truenine.component.security.models.SecurityPolicyDefineModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityPolicyConfig {
  @Bean
  fun policy(
    rbac: RbacAggregator,
    userService: UserService,
    mapper: ObjectMapper,
    jwtIssuer: JwtIssuer
  ): SecurityPolicyDefineModel {
    val se = SecurityPolicyDefineModel()
    se.exceptionAdware = ExceptionAdware()
    se.preValidFilter = PreValidFilter(mapper, jwtIssuer)
    se.service = SecurityServiceConfig(rbac, userService)
    // FIXME 添加过滤规则
    return se
  }
}

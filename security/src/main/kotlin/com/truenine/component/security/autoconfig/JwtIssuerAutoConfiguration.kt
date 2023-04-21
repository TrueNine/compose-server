package com.truenine.component.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.security.properties.JwtProperties
import com.truenine.component.security.jwt.JwtIssuer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtIssuerAutoConfiguration(
  private val jp: JwtProperties
) {
  @Bean
  @Primary
  fun jwtIssuer(mapper: ObjectMapper): JwtIssuer {
    // TODO 完成此类
    return JwtIssuer.createIssuer()
      .serializer(mapper)
      .encryptDataKeyName(jp.encryptDataKeyName)
      .issuer(jp.issuer)
      .expire(jp.expiredDuration)
      .build()
  }
}

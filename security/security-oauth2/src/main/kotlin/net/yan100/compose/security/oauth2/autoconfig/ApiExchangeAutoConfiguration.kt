package net.yan100.compose.security.oauth2.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.depend.webclient.lang.jsonWebClientRegister
import net.yan100.compose.security.oauth2.api.WechatMpAuthApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  @ConditionalOnBean(ObjectMapper::class)
  fun wechatJsApi(objectMapper: ObjectMapper): WechatMpAuthApi {
    return jsonWebClientRegister<WechatMpAuthApi>(objectMapper) { a, b ->
      a to b
    }
  }
}

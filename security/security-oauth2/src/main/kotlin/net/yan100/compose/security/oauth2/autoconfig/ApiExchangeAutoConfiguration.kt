package net.yan100.compose.security.oauth2.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.depend.webclient.lang.jsonWebClientRegister
import net.yan100.compose.security.oauth2.api.WechatMpAuthApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiExchangeAutoConfiguration {

  companion object {
    private val log = slf4j(ApiExchangeAutoConfiguration::class)
  }

  @Bean
  fun wechatJsApi(objectMapper: ObjectMapper): WechatMpAuthApi {
    return jsonWebClientRegister<WechatMpAuthApi>(objectMapper) { a, b ->
      a to b
    }
  }
}

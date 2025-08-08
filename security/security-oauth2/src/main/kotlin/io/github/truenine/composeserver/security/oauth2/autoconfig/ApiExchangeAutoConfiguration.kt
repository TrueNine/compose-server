package io.github.truenine.composeserver.security.oauth2.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.httpexchange.jsonWebClientRegister
import io.github.truenine.composeserver.security.oauth2.api.IWxMpApi
import io.github.truenine.composeserver.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = slf4j(ApiExchangeAutoConfiguration::class)

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wxMpApi(objectMapper: ObjectMapper): IWxMpApi {
    return jsonWebClientRegister<IWxMpApi>(objectMapper) { a, b -> a to b }
  }
}

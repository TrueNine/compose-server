package io.github.truenine.composeserver.psdk.wxpa.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.httpexchange.jsonWebClientRegister
import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = slf4j(ApiExchangeAutoConfiguration::class)

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wxpaApi(objectMapper: ObjectMapper): IWxpaWebClient {
    return jsonWebClientRegister<IWxpaWebClient>(objectMapper) { a, b -> a to b }
  }
}

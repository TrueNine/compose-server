package io.github.truenine.composeserver.psdk.wxpa.autoconfig

import io.github.truenine.composeserver.depend.httpexchange.jsonWebClientRegister
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper

private val log = logger<ApiExchangeAutoConfiguration>()

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wxpaApi(objectMapper: JsonMapper): IWxpaWebClient {
    return jsonWebClientRegister<IWxpaWebClient>(objectMapper) { a, b -> a to b }
  }
}

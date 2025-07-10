package net.yan100.compose.security.oauth2.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.depend.httpexchange.jsonWebClientRegister
import net.yan100.compose.security.oauth2.api.IWxMpApi
import net.yan100.compose.security.oauth2.api.IWxpaWebClient
import net.yan100.compose.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = slf4j(ApiExchangeAutoConfiguration::class)

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wxMpApi(objectMapper: ObjectMapper): IWxMpApi {
    return jsonWebClientRegister<IWxMpApi>(objectMapper) { a, b -> a to b }
  }

  @Bean
  fun wxpaApi(objectMapper: ObjectMapper): IWxpaWebClient {
    return jsonWebClientRegister<IWxpaWebClient>(objectMapper) { a, b -> a to b }
  }
}

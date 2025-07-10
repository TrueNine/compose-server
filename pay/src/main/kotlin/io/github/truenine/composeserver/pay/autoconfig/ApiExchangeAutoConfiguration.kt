package io.github.truenine.composeserver.pay.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.httpexchange.jsonWebClientRegister
import io.github.truenine.composeserver.pay.api.WechatPayV3JsApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wechatPayJsApi(objectMapper: ObjectMapper): WechatPayV3JsApi {
    return jsonWebClientRegister<WechatPayV3JsApi>(objectMapper) { a, b -> a to b }
  }
}

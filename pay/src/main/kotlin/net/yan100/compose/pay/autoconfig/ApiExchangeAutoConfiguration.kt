package net.yan100.compose.pay.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.depend.httpexchange.jsonWebClientRegister
import net.yan100.compose.pay.api.WechatPayV3JsApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wechatPayJsApi(objectMapper: ObjectMapper): WechatPayV3JsApi {
    return jsonWebClientRegister<WechatPayV3JsApi>(objectMapper) { a, b -> a to b }
  }
}

package io.github.truenine.composeserver.pay.wechat.autoconfig

import io.github.truenine.composeserver.depend.httpexchange.jsonWebClientRegister
import io.github.truenine.composeserver.pay.wechat.api.WechatPayV3JsApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wechatPayJsApi(objectMapper: JsonMapper): WechatPayV3JsApi {
    return jsonWebClientRegister<WechatPayV3JsApi>(objectMapper) { a, b -> a to b }
  }
}

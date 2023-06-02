package net.yan100.compose.pay.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.depend.webclient.lang.jsonWebClientRegister
import net.yan100.compose.pay.api.WechatPayV3JsApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wechatPayJsApi(objectMapper: ObjectMapper): WechatPayV3JsApi {
    return jsonWebClientRegister<WechatPayV3JsApi>(objectMapper) { a, b ->
      a to b.blockTimeout(Duration.of(30, ChronoUnit.SECONDS))
    }
  }
}

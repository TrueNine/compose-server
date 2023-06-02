package net.yan100.compose.pay.autoconfig

import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.depend.webclient.lang.webClientRegister
import net.yan100.compose.pay.api.WechatPayV3JsApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wechatPayJsApi(): WechatPayV3JsApi {
    return webClientRegister<WechatPayV3JsApi> { a, b ->
      a.defaultStatusHandler({ httpCode -> httpCode.isError })
      { resp -> RemoteCallException(msg = resp.toString(), code = resp.statusCode().value()).toMono() }
        .build() to
        b.blockTimeout(Duration.of(30, ChronoUnit.SECONDS)).build()
    }
  }


}

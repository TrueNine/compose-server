package net.yan100.compose.pay.autoconfig

import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.pay.api.WechatPayJsApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
class ApiExchangeAutoConfiguration {
  @Bean
  fun wechatPayJsApi(): WechatPayJsApi {
    val client = WebClient.builder()
      // 错误拦截器
      .defaultStatusHandler({ httpCode -> httpCode.isError })
      // 异常返回
      { resp -> RemoteCallException(msg = resp.toString(), code = resp.statusCode().value()).toMono() }
      .build()

    val factory = HttpServiceProxyFactory
      .builder(WebClientAdapter.forClient(client))
      .blockTimeout(Duration.of(30, ChronoUnit.SECONDS))
      .build()
    return factory.createClient<WechatPayJsApi>()
  }
}

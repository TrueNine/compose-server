package io.github.truenine.composeserver.data.extract.autoconfig

import io.github.truenine.composeserver.data.extract.api.ICnNbsAddressApi
import io.github.truenine.composeserver.exceptions.RemoteCallException
import io.github.truenine.composeserver.slf4j
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.kotlin.core.publisher.toMono
import reactor.netty.http.client.HttpClient

private val log = slf4j<ApiExchangesAutoConfiguration>()

@Configuration
class ApiExchangesAutoConfiguration {
  @Bean
  fun cnNbsAddressApi(): ICnNbsAddressApi {
    log.debug("创建 中国统计局地址 api")
    val sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
    val unsafeConnector =
      ReactorClientHttpConnector(HttpClient.create().secure { t -> t.sslContext(sslCtx) }.compress(true).resolver(DefaultAddressResolverGroup.INSTANCE))

    val client =
      WebClient.builder()
        .clientConnector(unsafeConnector)
        .defaultHeaders { it["Accept-Charset"] = "utf-8" }
        .defaultStatusHandler({ httpCode -> httpCode.isError }) { resp -> RemoteCallException(msg = resp.toString()).toMono() }
        .build()
    val factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build()
    return factory.createClient(ICnNbsAddressApi::class.java)
  }
}

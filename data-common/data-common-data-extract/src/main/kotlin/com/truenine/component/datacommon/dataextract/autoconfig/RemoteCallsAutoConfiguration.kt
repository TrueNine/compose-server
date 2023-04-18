package com.truenine.component.datacommon.dataextract.autoconfig

import com.truenine.component.core.exceptions.RemoteCallException
import com.truenine.component.core.lang.LogKt
import com.truenine.component.datacommon.dataextract.interfaces.CnNbsAddressRemoteCall
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient
import reactor.kotlin.core.publisher.toMono

@Configuration
class RemoteCallsAutoConfiguration {

  @Bean
  fun cnNbsAddressRemoteCall(): CnNbsAddressRemoteCall {
    log.debug("创建 国家统计局 WebClient")
    val client = WebClient.builder()
      .defaultStatusHandler({ httpCode ->
        httpCode.isError
      }) { resp ->
        RemoteCallException(msg = resp.toString(), code = resp.statusCode().value()).toMono()
      }
      .build()
    val factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build()
    return factory.createClient<CnNbsAddressRemoteCall>()
  }

  private val log = LogKt.getLog(this::class)
}

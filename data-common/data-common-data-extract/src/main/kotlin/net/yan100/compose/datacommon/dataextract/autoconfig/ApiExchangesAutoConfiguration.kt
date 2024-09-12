/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.datacommon.dataextract.autoconfig

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.resolver.DefaultAddressResolverGroup
import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.core.slf4j
import net.yan100.compose.datacommon.dataextract.api.ICnNbsAddressApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.kotlin.core.publisher.toMono
import reactor.netty.http.client.HttpClient

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
        .defaultStatusHandler({ httpCode -> httpCode.isError }) { resp ->
          RemoteCallException(msg = resp.toString(), code = resp.statusCode().value()).toMono()
        }
        .build()
    val factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build()
    return factory.createClient(ICnNbsAddressApi::class.java)
  }

  private val log = slf4j(this::class)
}

package net.yan100.compose.depend.webclient.lang

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.service.invoker.HttpServiceProxyFactory

inline fun <reified T : Any> webClientRegister(
  builder: (client: WebClient.Builder, factory: HttpServiceProxyFactory.Builder) -> Pair<WebClient, HttpServiceProxyFactory>
): T {
  val clientBuilder = WebClient.builder()
  val factoryBuilder = HttpServiceProxyFactory.builder()
  val a = builder(clientBuilder, factoryBuilder)
  return a.second.createClient(T::class.java)
}

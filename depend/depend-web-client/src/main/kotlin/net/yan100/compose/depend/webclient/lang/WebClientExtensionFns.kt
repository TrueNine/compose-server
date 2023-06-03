package net.yan100.compose.depend.webclient.lang

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.MediaTypes
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.util.MimeType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

/**
 * # 自定义的json编解码器
 * 使用你自定义的jackson ObjectMapper创建
 * @param objectMapper mapper
 * @return WebClient
 * @see [WebClient]
 * @see [ObjectMapper]
 * @see [org.springframework.web.service.annotation.HttpExchange]
 */
inline fun <reified T : Any> jsonWebClientRegister(
  objectMapper: ObjectMapper,
  builder: (client: WebClient.Builder, factory: HttpServiceProxyFactory.Builder) -> Pair<WebClient.Builder, HttpServiceProxyFactory.Builder>
): T {
  val clientBuilder = WebClient.builder()
  val factoryBuilder = HttpServiceProxyFactory.builder()
  val jsonHandleMimeTypes = arrayOf(
    MimeType.valueOf(MediaTypes.JSON.getValue()!!),
    MimeType.valueOf("application/*+json"),
    MimeType.valueOf("application/x-ndjson"),
    MimeType.valueOf(MediaTypes.TEXT.getValue()!!),
  )
  clientBuilder.codecs { it ->
    it.defaultCodecs().jackson2JsonDecoder(
      Jackson2JsonDecoder(
        objectMapper,
        *jsonHandleMimeTypes
      )
    )
    it.defaultCodecs().jackson2JsonEncoder(
      Jackson2JsonEncoder(
        objectMapper,
        *jsonHandleMimeTypes
      )
    )
    println()
  }

  clientBuilder.defaultHeader(Headers.ACCEPT, MediaTypes.JSON.media(), MediaTypes.TEXT.media())

  val cf = builder(clientBuilder, factoryBuilder)
  val client = cf.first.build()
  return cf.second.clientAdapter(WebClientAdapter.forClient(client)).build().createClient(T::class.java)
}

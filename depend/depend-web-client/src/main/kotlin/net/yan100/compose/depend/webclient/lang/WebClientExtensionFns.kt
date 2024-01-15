package net.yan100.compose.depend.webclient.lang

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.MediaTypes
import net.yan100.compose.core.lang.AnyTyping
import net.yan100.compose.depend.webclient.encoder.AnyTypingEncoder
import org.springframework.core.MethodParameter
import org.springframework.http.codec.EncoderHttpMessageWriter
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.time.Duration
import java.time.temporal.ChronoUnit

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
  timeout: Duration = Duration.of(10, ChronoUnit.SECONDS),
  builder: (client: WebClient.Builder, factory: HttpServiceProxyFactory.Builder) -> Pair<WebClient.Builder, HttpServiceProxyFactory.Builder>
): T {
  val clientBuilder = WebClient.builder()
  val factoryBuilder = HttpServiceProxyFactory.builder()
  val jsonHandleMimeTypes = arrayOf(
    MimeType.valueOf(MediaTypes.JSON.value),
    MimeType.valueOf("application/*+json"),
    MimeType.valueOf("application/x-ndjson"),
    MimeType.valueOf(MediaTypes.TEXT.value),
  )
  clientBuilder.codecs {
    it.defaultCodecs().enableLoggingRequestDetails(true)

    it.writers.addFirst(EncoderHttpMessageWriter(AnyTypingEncoder()))

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
  }

  clientBuilder.defaultHeader(Headers.ACCEPT, MediaTypes.JSON.value, MediaTypes.TEXT.value)

  val cf = builder(clientBuilder, factoryBuilder)
  val client = cf.first.build()
  return cf.second
    .customArgumentResolver(ArgsResolver())

    .exchangeAdapter(WebClientAdapter.create(client).apply { blockTimeout = timeout })
    .build()
    .createClient(T::class.java)
}

class ArgsResolver : HttpServiceArgumentResolver {

  override fun resolve(argument: Any?, parameter: MethodParameter, requestValues: HttpRequestValues.Builder): Boolean {
    if (argument != null && argument is AnyTyping) {
      val name = parameter.getParameterAnnotation(RequestParam::class.java)?.name ?: parameter.getParameterAnnotation(RequestParam::class.java)?.value
      ?: parameter.parameterName ?: throw IllegalArgumentException("参数解析异常")
      requestValues.addRequestParameter(
        name, argument.value.toString()
      )
      return true
    }
    return false
  }
}

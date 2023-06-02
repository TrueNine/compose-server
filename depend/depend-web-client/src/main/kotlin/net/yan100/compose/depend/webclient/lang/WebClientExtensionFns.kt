package net.yan100.compose.depend.webclient.lang

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.MediaTypes
import org.springframework.boot.convert.ApplicationConversionService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.util.MimeType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory


inline fun <reified T : Any> jsonWebClientRegister(
  objectMapper: ObjectMapper,
  builder: (client: WebClient.Builder, factory: HttpServiceProxyFactory.Builder) -> Pair<WebClient.Builder, HttpServiceProxyFactory.Builder>
): T {
  val clientBuilder = WebClient.builder()
  val factoryBuilder = HttpServiceProxyFactory.builder()
  factoryBuilder.conversionService(JsonResultConverter(objectMapper))
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


class JsonResultConverter(
  private val mapper: ObjectMapper
) : ApplicationConversionService() {
  override fun canConvert(sourceType: Class<*>?, targetType: Class<*>): Boolean {
    return true
  }

  override fun canConvert(sourceType: TypeDescriptor?, targetType: TypeDescriptor): Boolean {
    return true
  }

  override fun <T : Any?> convert(source: Any?, targetType: Class<T>): T? {
    return super.convert(source, targetType)
  }

  override fun convert(source: Any?, sourceType: TypeDescriptor?, targetType: TypeDescriptor): Any? {
    return super.convert(source, sourceType, targetType)
  }
}

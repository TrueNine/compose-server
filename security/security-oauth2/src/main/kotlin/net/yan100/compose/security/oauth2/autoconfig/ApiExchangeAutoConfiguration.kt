package net.yan100.compose.security.oauth2.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.lang.pnt
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.depend.webclient.lang.jsonWebClientRegister
import net.yan100.compose.security.oauth2.api.WechatMpAuthApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.core.codec.Encoder
import org.springframework.http.codec.HttpMessageReader
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver

@Configuration
class ApiExchangeAutoConfiguration {

  companion object {
    private val log = slf4j(ApiExchangeAutoConfiguration::class)
  }

  @Bean
  fun wechatJsApi(objectMapper: ObjectMapper): WechatMpAuthApi {
    return jsonWebClientRegister<WechatMpAuthApi>(objectMapper) { a, b ->
      a to b
    }
  }
}

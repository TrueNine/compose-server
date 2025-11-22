package io.github.truenine.composeserver.depend.servlet.autoconfig

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.depend.servlet.converters.ToolsJacksonHttpMessageConverter
import io.github.truenine.composeserver.depend.servlet.resolvers.IPageParamLikeArgumentResolver
import io.github.truenine.composeserver.slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.databind.ObjectMapper

@Configuration
@Import(IPageParamLikeArgumentResolver::class)
class DefaultDependServletWebMvcAutoConfiguration(
  private val iPageParamLikeArgumentResolver: IPageParamLikeArgumentResolver,
  @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private val defaultObjectMapper: ObjectMapper,
) : WebMvcConfigurer {
  companion object {
    @JvmStatic private val log = slf4j<DefaultDependServletWebMvcAutoConfiguration>()
  }

  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    super.addArgumentResolvers(resolvers)
    resolvers.add(iPageParamLikeArgumentResolver)
    log.trace("addArgumentResolvers: {}", resolvers)
  }

  override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
    super.extendMessageConverters(converters)
    converters.removeIf { it is ToolsJacksonHttpMessageConverter }
    converters.add(0, ToolsJacksonHttpMessageConverter(defaultObjectMapper))
    log.trace("registered ToolsJacksonHttpMessageConverter at the beginning of converters list")
  }
}

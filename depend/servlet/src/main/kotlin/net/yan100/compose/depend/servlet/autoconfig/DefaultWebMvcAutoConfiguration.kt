package net.yan100.compose.depend.servlet.autoconfig

import net.yan100.compose.depend.servlet.resolvers.IPageParamLikeArgumentResolver
import net.yan100.compose.slf4j
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class DefaultWebMvcAutoConfiguration(
  private val iPageParamLikeArgumentResolver: IPageParamLikeArgumentResolver,
) : WebMvcConfigurer {
  companion object {
    @JvmStatic
    private val log = slf4j<DefaultWebMvcAutoConfiguration>()
  }

  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
    super.addArgumentResolvers(resolvers)
    resolvers.add(iPageParamLikeArgumentResolver)
    log.trace("addArgumentResolvers: {}", resolvers)
  }
}

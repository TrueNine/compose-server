package net.yan100.compose.depend.servlet.autoconfig

import net.yan100.compose.depend.servlet.resolvers.IPageParamLikeArgumentResolver
import net.yan100.compose.slf4j
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Import(IPageParamLikeArgumentResolver::class)
class DefaultDependServletWebMvcAutoConfiguration(
  private val iPageParamLikeArgumentResolver: IPageParamLikeArgumentResolver
) : WebMvcConfigurer {
  companion object {
    @JvmStatic
    private val log = slf4j<DefaultDependServletWebMvcAutoConfiguration>()
  }

  override fun addArgumentResolvers(
    resolvers: MutableList<HandlerMethodArgumentResolver?>
  ) {
    super.addArgumentResolvers(resolvers)
    resolvers.add(iPageParamLikeArgumentResolver)
    log.trace("addArgumentResolvers: {}", resolvers)
  }
}

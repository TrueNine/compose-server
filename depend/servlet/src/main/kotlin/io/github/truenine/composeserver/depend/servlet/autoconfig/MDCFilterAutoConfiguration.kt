package io.github.truenine.composeserver.depend.servlet.autoconfig

import io.github.truenine.composeserver.depend.servlet.filter.MDCFilter
import net.yan100.compose.slf4j
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

private val log = slf4j(MDCFilterAutoConfiguration::class)

@Configuration
class MDCFilterAutoConfiguration {
  inner class CustomerMDCFilter : MDCFilter()

  @Bean
  fun mdcFilter(): FilterRegistrationBean<CustomerMDCFilter> {
    val bean = FilterRegistrationBean<CustomerMDCFilter>()
    bean.filter = CustomerMDCFilter()
    bean.urlPatterns = listOf("/*")
    bean.order = Ordered.HIGHEST_PRECEDENCE
    log.debug("注册 MDCFilter 并列为首个过滤器 bean = {}", bean)
    return bean
  }
}

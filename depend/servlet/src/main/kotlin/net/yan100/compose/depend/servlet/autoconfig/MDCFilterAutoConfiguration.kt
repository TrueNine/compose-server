package net.yan100.compose.depend.servlet.autoconfig

import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.servlet.filter.MDCFilter
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

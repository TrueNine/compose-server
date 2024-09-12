/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.depend.webservlet.autoconfig

import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.webservlet.filter.MDCFilter
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

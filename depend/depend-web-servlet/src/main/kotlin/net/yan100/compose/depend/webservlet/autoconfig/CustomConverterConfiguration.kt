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

import net.yan100.compose.core.log.slf4j
import net.yan100.compose.depend.webservlet.converters.JavaLocalDateConverter
import net.yan100.compose.depend.webservlet.converters.JavaLocalDateTimeConverter
import net.yan100.compose.depend.webservlet.converters.JavaLocalTimeConverter
import net.yan100.compose.depend.webservlet.converters.StringArrayToByteArrayConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

private val log = slf4j(CustomConverterConfiguration::class)

@Configuration
class CustomConverterConfiguration {
  @Bean
  @Primary
  fun stringArrayToByteArrayConverter(): StringArrayToByteArrayConverter {
    log.debug("注册 自定义字符数组到字节数组转换器并列为首位")
    return StringArrayToByteArrayConverter()
  }

  @Bean
  @Primary
  fun timestampToLocalDateTimeConverter(): JavaLocalDateTimeConverter {
    log.debug("注册 日期时间序列化器并列为首位")
    return JavaLocalDateTimeConverter()
  }

  @Bean
  @Primary
  fun timestampToLocalDateConverter(): JavaLocalDateConverter {
    log.debug("注册 日期序列化器并列为首位")
    return JavaLocalDateConverter()
  }

  @Bean
  @Primary
  fun timestampToLocalTimeConverter(): JavaLocalTimeConverter {
    log.debug("注册 时间序列化器并列为首位")
    return JavaLocalTimeConverter()
  }
}

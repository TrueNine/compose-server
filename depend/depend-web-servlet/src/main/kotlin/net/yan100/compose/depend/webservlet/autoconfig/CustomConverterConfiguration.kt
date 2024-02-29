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

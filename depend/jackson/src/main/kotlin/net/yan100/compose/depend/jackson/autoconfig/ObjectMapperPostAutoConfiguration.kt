package net.yan100.compose.depend.jackson.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.jackson.holders.ObjectMapperHolder
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration

private val log = slf4j<ObjectMapperPostAutoConfiguration>()

@Configuration
class ObjectMapperPostAutoConfiguration : BeanPostProcessor {
  override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
    if (bean is ObjectMapper) {
      log.trace("注入全局 jackson holder bean: {} beanName: {}", bean, beanName)
      ObjectMapperHolder.set(bean)
    }
    return super.postProcessAfterInitialization(bean, beanName)
  }
}

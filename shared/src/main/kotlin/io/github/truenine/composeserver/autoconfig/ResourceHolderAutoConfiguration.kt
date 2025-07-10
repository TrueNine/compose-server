package io.github.truenine.composeserver.autoconfig

import io.github.truenine.composeserver.holders.ResourceHolder
import io.github.truenine.composeserver.properties.DataLoadProperties
import org.springframework.boot.system.ApplicationHome
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader

@Configuration
class ResourceHolderAutoConfiguration(private val p: DataLoadProperties) {

  @Bean
  fun resourceHolder(defaultResourceLoader: DefaultResourceLoader): ResourceHolder {
    val home = ApplicationHome()
    val res = ResourceHolder(home, defaultResourceLoader, p)
    return res
  }
}

package net.yan100.compose.autoconfig

import net.yan100.compose.holders.ResourceHolder
import net.yan100.compose.properties.DataLoadProperties
import org.springframework.boot.system.ApplicationHome
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader

@Configuration
class ResourceHolderAutoConfiguration(private val p: DataLoadProperties) {

  @Bean
  fun resourceHolder(
    defaultResourceLoader: DefaultResourceLoader
  ): ResourceHolder {
    val home = ApplicationHome()
    val res = ResourceHolder(home, defaultResourceLoader, p)
    return res
  }
}

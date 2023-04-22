package com.truenine.component.security.autoconfig

import com.truenine.component.core.encrypt.FileKeysRepository
import com.truenine.component.core.encrypt.KeysRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FileKeysRepositoryAutoConfiguration {
  @Bean
  fun fileKeysRepository(): KeysRepository {
    return FileKeysRepository()
  }
}

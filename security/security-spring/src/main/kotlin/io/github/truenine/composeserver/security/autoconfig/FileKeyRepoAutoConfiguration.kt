package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.security.crypto.FileKeyRepo
import io.github.truenine.composeserver.slf4j
import org.springframework.context.annotation.*

@Configuration
class FileKeyRepoAutoConfiguration {
  companion object {
    @JvmStatic private val log = slf4j(FileKeyRepoAutoConfiguration::class)
  }

  @Bean
  @Primary
  fun fileKeyRepo(): FileKeyRepo {
    log.debug("Register file-based key repository")
    return FileKeyRepo()
  }
}

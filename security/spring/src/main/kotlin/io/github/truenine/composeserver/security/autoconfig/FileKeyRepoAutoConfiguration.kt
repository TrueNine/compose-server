package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.security.crypto.FileKeyRepo
import io.github.truenine.composeserver.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FileKeyRepoAutoConfiguration {
  companion object {
    @JvmStatic private val log = slf4j(FileKeyRepoAutoConfiguration::class)
  }

  @Bean
  @Primary
  fun fileKeyRepo(): FileKeyRepo {
    log.debug("注册 以文件形式获取密钥")
    return FileKeyRepo()
  }
}

package net.yan100.compose.security.autoconfig

import net.yan100.compose.security.crypto.FileKeyRepo
import net.yan100.compose.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FileKeyRepoAutoConfiguration {
  companion object {
    @JvmStatic
    private val log = slf4j(FileKeyRepoAutoConfiguration::class)
  }

  @Bean
  @Primary
  fun fileKeyRepo(): FileKeyRepo {
    log.debug("注册 以文件形式获取密钥")
    return FileKeyRepo()
  }
}

package com.truenine.component.security.autoconfig

import com.truenine.component.core.encrypt.FileKeysRepository
import com.truenine.component.core.encrypt.KeysRepository
import com.truenine.component.core.lang.slf4j
import com.truenine.component.core.properties.KeysProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FileKeysRepositoryAutoConfiguration(
  private val kp: KeysProperties
) {
  private val log = slf4j(this::class)

  @Bean
  fun fileKeysRepository(): KeysRepository {
    log.trace("注册文件密钥 = {}", kp)
    val fp = FileKeysRepository(
      keyDest = kp.dir,
      eccKeyPairPaths = kp.eccPublicKeyPath to kp.eccPrivateKeyPath,
      rsaKeyPairPaths = kp.rsaPublicKeyPath to kp.eccPrivateKeyPath,
      aesPaths = kp.aesKeyPath
    )
    log.trace("已注册成功 = {}", kp)
    return fp
  }
}

package net.yan100.compose.security.autoconfig


import net.yan100.compose.core.encrypt.FileKeysRepository
import net.yan100.compose.core.encrypt.KeysRepository
import net.yan100.compose.core.lang.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FileKeysRepositoryAutoConfiguration(
  private val kp: net.yan100.compose.core.properties.KeysProperties
) {
  private val log = slf4j(this::class)

  @Bean
  fun fileKeysRepository(): KeysRepository {
    log.trace("注册文件密钥 = {}", kp)
    val fp = FileKeysRepository(
      keyDest = kp.dir,
      eccKeyPairPaths = kp.eccPublicKeyPath to kp.eccPrivateKeyPath,
      rsaKeyPairPaths = kp.rsaPublicKeyPath to kp.rsaPrivateKeyPath,
      aesPaths = kp.aesKeyPath
    )
    log.trace("已注册成功 = {}", kp)
    return fp
  }
}

package net.yan100.compose.security.crypto

import net.yan100.compose.testtookit.log
import kotlin.test.Test
import kotlin.test.assertEquals

class EncryptorsTest {

  @Test
  fun `测试 sha-256 加密`() {
    val sha256signature = Encryptors.signatureBySha256("hello world")
    log.info("sha256 signature: {}", sha256signature)
    assertEquals(
      sha256signature,
      "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9",
    )
  }
}

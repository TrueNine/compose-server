package net.yan100.compose.security.crpyot

import kotlin.test.assertTrue
import net.yan100.compose.hasText
import net.yan100.compose.security.crypto.Keys
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test

class KeysTest {
  @Test
  fun `write key to pem`() {
    val a = Keys.writeKeyToPem(Keys.generateRsaKeyPair()!!.privateKey)
    val b = Keys.writeKeyToPem(Keys.generateEccKeyPair()!!.privateKey)

    log.info(a)
    log.info(b)

    assertTrue(a.hasText())
    assertTrue(b.hasText())
    assertTrue(a.contains("-----END RSA PKCS#8-----"))
    assertTrue(b.contains("-----END EC PKCS#8-----"))
  }
}

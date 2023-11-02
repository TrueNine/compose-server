package net.yan100.compose.core.encrypt

import org.junit.jupiter.api.Test

class KeysTest {
  @Test
  fun `test write key to pem`() {
    val a = Keys.writeKeyToPem(Keys.generateRsaKeyPair()!!.rsaPrivateKey!!)
    val b = Keys.writeKeyToPem(Keys.generateEccKeyPair()!!.eccPrivateKey!!)
    println(a)
  }
}

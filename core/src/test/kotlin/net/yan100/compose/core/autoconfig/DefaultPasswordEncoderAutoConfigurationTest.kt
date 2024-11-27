package net.yan100.compose.core.autoconfig

import jakarta.annotation.Resource
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class DefaultPasswordEncoderAutoConfigurationTest {
  lateinit var passwordEncoder: PasswordEncoder @Resource set

  @Test
  fun `ensure password encoder bean available`() {
    assertNotNull(passwordEncoder)
    val plainText = "test text"
    val encoded = passwordEncoder.encode(plainText)
    log.info("p: {}, e: {}", plainText, encoded)
    assertNotEquals(plainText, encoded)

    assertTrue { passwordEncoder.matches(plainText, encoded) }
  }
}

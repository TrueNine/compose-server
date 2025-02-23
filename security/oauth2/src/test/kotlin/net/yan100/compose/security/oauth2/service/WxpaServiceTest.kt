package net.yan100.compose.security.oauth2.service

import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class WxpaServiceTest {
  lateinit var wxpaService: WxpaService @Resource set

  @Test
  fun `确保 service 已经被注册`() {
    assertNotNull(wxpaService)
  }
}

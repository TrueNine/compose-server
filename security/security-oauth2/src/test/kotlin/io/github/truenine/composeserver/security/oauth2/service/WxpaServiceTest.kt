package io.github.truenine.composeserver.security.oauth2.service

import io.github.truenine.composeserver.security.oauth2.SecurityOauth2Entrance
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [SecurityOauth2Entrance::class], webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WxpaServiceTest {
  lateinit var wxpaService: WxpaService
    @Resource set

  @Test
  fun `确保 service 已经被注册`() {
    assertNotNull(wxpaService)
  }
}

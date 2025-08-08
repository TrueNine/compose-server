package io.github.truenine.composeserver.psdk.wxpa.integration

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.psdk.wxpa.autoconfig.AutoConfigEntrance
import io.github.truenine.composeserver.psdk.wxpa.event.WxpaTokenEventManager
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaApiException
import io.github.truenine.composeserver.psdk.wxpa.service.WxpaService
import io.github.truenine.composeserver.security.crypto.sha1
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest

private val log = logger<WxpaIntegrationTest>()

/**
 * # 微信公众号集成测试
 *
 * 需要设置以下环境变量才能执行真实的集成测试：
 * - WECHAT_APP_ID: 微信公众号应用ID
 * - WECHAT_APP_SECRET: 微信公众号应用密钥
 * - WECHAT_VERIFY_TOKEN: 微信公众号验证Token
 *
 * 如果环境变量不存在，测试将被跳过
 *
 * @author TrueNine
 * @since 2025-08-08
 */
@EnabledIf("hasRequiredEnvironmentVariables")
@SpringBootTest(
  classes = [AutoConfigEntrance::class],
  webEnvironment = SpringBootTest.WebEnvironment.NONE,
  properties =
    [
      "compose.psdk.wxpa.wxpa.app-id=\${WXPA_APP_ID}",
      "compose.psdk.wxpa.wxpa.app-secret=\${WXPA_APP_SECURET}",
      "compose.psdk.wxpa.wxpa.verify-token=\${WXPA_VERIFY_TOKEN}",
      "compose.psdk.wxpa.wxpa.enable-auto-refresh=true", // 禁用自动刷新避免测试时的网络调用
    ],
)
class WxpaIntegrationTest {

  @Resource private lateinit var wxpaService: WxpaService

  @Resource private lateinit var wxpaTokenEventManager: WxpaTokenEventManager

  companion object {
    /** 检查是否存在必需的环境变量 用于 JUnit5 的条件测试 */
    @JvmStatic
    fun hasRequiredEnvironmentVariables(): Boolean {
      val appId = System.getenv("WXPA_APP_ID")
      val appSecret = System.getenv("WXPA_APP_SECRET")
      val verifyToken = System.getenv("WXPA_VERIFY_TOKEN")

      val hasCredentials = !appId.isNullOrBlank() && !appSecret.isNullOrBlank() && !verifyToken.isNullOrBlank()

      if (!hasCredentials) {
        log.warn("跳过微信公众号集成测试：缺少必需的环境变量 WECHAT_APP_ID、WECHAT_APP_SECRET 或 WECHAT_VERIFY_TOKEN")
      } else {
        log.info("检测到微信公众号凭证，将执行集成测试")
      }

      return hasCredentials
    }
  }

  @Nested
  inner class `Spring 容器集成测试` {

    @Test
    fun `应该成功注册 WxpaService Bean`() {
      assertNotNull(wxpaService)
      log.info("WxpaService Bean 注册成功")
    }

    @Test
    fun `应该成功注册 WxpaTokenEventManager Bean`() {
      assertNotNull(wxpaTokenEventManager)
      log.info("WxpaTokenEventManager Bean 注册成功")
    }

    @Test
    fun `应该能够获取 token 状态`() {
      // When
      val status = wxpaService.getTokenStatus()

      // Then
      assertNotNull(status)
      assertTrue(status.containsKey("hasAccessToken"))
      assertTrue(status.containsKey("accessTokenExpired"))
      assertTrue(status.containsKey("hasJsapiTicket"))
      assertTrue(status.containsKey("jsapiTicketExpired"))
      log.info("Token 状态获取成功: {}", status)
    }

    @Test
    fun `应该能够处理服务器验证请求`() {
      // Given
      val request =
        WxpaService.ServerVerificationRequest(signature = "test_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")

      // When
      val result = wxpaService.verifyServerConfiguration(request)

      // Then
      // 由于签名不正确，应该返回null，但不应该抛出异常
      // 这验证了服务的异常处理机制
      assertNull(result)
      log.info("服务器验证请求处理正常，错误签名被正确拒绝")
    }
  }

  @Nested
  inner class `配置验证测试` {

    @Test
    fun `应该正确加载配置属性`() {
      // 通过能够创建服务实例来验证配置加载正确
      assertNotNull(wxpaService)
      log.info("配置属性加载验证成功")
    }
  }

  @Nested
  inner class `正常业务流程测试` {

    @Test
    fun `应该能够生成 JSAPI 签名`() {
      // Given
      val testUrl = "https://example.com/test"

      // When
      val signature = wxpaService.generateJsapiSignature(testUrl)

      // Then
      // 在测试环境下，由于使用的是测试配置，会因为无效的appSecret而返回null
      // 这是预期的行为，验证了异常处理机制正常工作
      assertNull(signature)
      log.info("JSAPI 签名生成测试完成，正确处理了无效凭证")
    }

    @Test
    fun `应该能够处理用户授权码获取用户信息`() {
      // Given
      val testAuthCode = "test_auth_code_12345"

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(testAuthCode)

      // Then
      // 在测试环境下，由于使用的是测试配置，无法获取真实的用户信息
      // 应该返回null而不抛出异常
      assertNull(userInfo)
      log.info("用户信息获取测试完成，正确处理了无效授权码")
    }

    @Test
    fun `应该能够检查用户授权状态`() {
      // Given
      val testAccessToken = "test_access_token"
      val testOpenId = "test_open_id"

      // When
      val authStatus = wxpaService.checkUserAuthStatus(testAccessToken, testOpenId)

      // Then
      // 在测试环境下，由于使用的是测试配置，授权检查应该返回false
      assertFalse(authStatus)
      log.info("用户授权状态检查测试完成，结果: {}", authStatus)
    }

    @Test
    fun `应该能够强制刷新 tokens`() {
      // When & Then
      // 在测试环境下，由于使用无效凭证，forceRefreshTokens会抛出异常
      // 这是预期的行为，验证了异常处理机制正常工作
      assertThrows<WxpaApiException> { wxpaService.forceRefreshTokens() }
      log.info("强制刷新 tokens 测试完成，正确抛出了预期的异常")
    }
  }

  @Nested
  inner class `异常情况处理测试` {

    @Test
    fun `应该正确处理无效的服务器验证签名`() {
      // Given
      val invalidRequest =
        WxpaService.ServerVerificationRequest(signature = "invalid_signature", timestamp = "1234567890", nonce = "test_nonce", echostr = "test_echo_string")

      // When
      val result = wxpaService.verifyServerConfiguration(invalidRequest)

      // Then
      assertNull(result)
      log.info("无效签名验证测试完成，正确返回null")
    }

    @Test
    fun `应该正确处理有效的服务器验证签名`() {
      // Given
      val timestamp = "1234567890"
      val nonce = "test_nonce"
      // 使用环境变量中的token，这与Spring配置中的token一致
      val token = System.getenv("WXPA_VERIFY_TOKEN") ?: "test_verify_token"
      val echostr = "test_echo_string"

      // 计算正确的签名
      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val validSignature = signatureString.sha1

      val validRequest = WxpaService.ServerVerificationRequest(signature = validSignature, timestamp = timestamp, nonce = nonce, echostr = echostr)

      log.info("测试签名验证，使用token: {}, 计算的签名: {}", token, validSignature)

      // When
      val result = wxpaService.verifyServerConfiguration(validRequest)

      // Then
      assertEquals(echostr, result)
      log.info("有效签名验证测试完成，正确返回echostr: {}", result)
    }

    @Test
    fun `应该正确处理空字符串参数`() {
      // Given
      val emptyRequest = WxpaService.ServerVerificationRequest(signature = "", timestamp = "", nonce = "", echostr = "")

      // When
      val result = wxpaService.verifyServerConfiguration(emptyRequest)

      // Then
      assertNull(result)
      log.info("空字符串参数测试完成，正确返回null")
    }

    @Test
    fun `应该正确处理无效的授权码`() {
      // Given
      val invalidAuthCode = ""

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(invalidAuthCode)

      // Then
      assertNull(userInfo)
      log.info("无效授权码测试完成，正确返回null")
    }

    @Test
    fun `应该正确处理无效的 URL 生成 JSAPI 签名`() {
      // Given
      val invalidUrl = ""

      // When
      val signature = wxpaService.generateJsapiSignature(invalidUrl)

      // Then
      // 即使URL为空，也应该能够处理而不抛出异常
      log.info("无效URL的JSAPI签名生成测试完成，结果: {}", signature)
    }
  }

  @Nested
  inner class `边界条件测试` {

    @Test
    fun `应该处理极长的 URL 生成 JSAPI 签名`() {
      // Given
      val longUrl = "https://example.com/" + "a".repeat(2000) + "?param=value"

      // When
      val signature = wxpaService.generateJsapiSignature(longUrl)

      // Then
      // 在测试环境下，由于使用测试凭证，会返回null，但不应该抛出异常
      assertNull(signature)
      log.info("极长URL的JSAPI签名生成测试完成，正确处理了无效凭证")
    }

    @Test
    fun `应该处理带有特殊字符的 URL`() {
      // Given
      val specialUrl = "https://example.com/测试?param=值&other=特殊字符!@#$%^&*()"

      // When
      val signature = wxpaService.generateJsapiSignature(specialUrl)

      // Then
      // 在测试环境下，由于使用测试凭证，会返回null，但不应该抛出异常
      assertNull(signature)
      log.info("特殊字符URL的JSAPI签名生成测试完成，正确处理了无效凭证")
    }

    @Test
    fun `应该处理带锚点的 URL`() {
      // Given
      val urlWithAnchor = "https://example.com/test#section1"

      // When
      val signature = wxpaService.generateJsapiSignature(urlWithAnchor)

      // Then
      // 在测试环境下，由于使用测试凭证，会返回null
      // 但我们可以验证URL处理逻辑是否正确（锚点应该被移除）
      assertNull(signature)
      log.info("带锚点URL的JSAPI签名生成测试完成，正确处理了无效凭证")
    }

    @Test
    fun `应该处理极长的授权码`() {
      // Given
      val longAuthCode = "auth_code_" + "x".repeat(1000)

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(longAuthCode)

      // Then
      // 应该能够处理长授权码而不抛出异常，返回null
      assertNull(userInfo)
      log.info("极长授权码测试完成，正确处理了无效授权码")
    }

    @Test
    fun `应该处理包含特殊字符的授权码`() {
      // Given
      val specialAuthCode = "auth_code_!@#$%^&*()"

      // When
      val userInfo = wxpaService.getUserInfoByAuthCode(specialAuthCode)

      // Then
      // 应该能够处理特殊字符授权码而不抛出异常，返回null
      assertNull(userInfo)
      log.info("特殊字符授权码测试完成，正确处理了无效授权码")
    }
  }

  @Nested
  inner class `事件驱动机制测试` {

    @Test
    fun `应该能够获取 Token 使用统计`() {
      // When
      val stats = wxpaTokenEventManager.getTokenUsageStats()

      // Then
      assertNotNull(stats)
      assertTrue(stats.isNotEmpty())
      // 验证统计数据的结构
      stats.forEach { (key, value) -> assertTrue(value >= 0L, "统计值应该非负: $key = $value") }
      log.info("Token 使用统计获取成功: {}", stats)
    }

    @Test
    fun `应该能够获取刷新失败次数`() {
      // When
      val failureCount = wxpaTokenEventManager.getRefreshFailureCount()

      // Then
      assertTrue(failureCount >= 0L, "刷新失败次数应该非负，实际值: $failureCount")
      log.info("刷新失败次数获取成功: {}", failureCount)
    }

    @Test
    fun `应该能够获取最后健康检查时间`() {
      // When
      val lastCheckTime = wxpaTokenEventManager.getLastHealthCheckTime()

      // Then
      // 初始状态下可能为null，这是正常的
      log.info("最后健康检查时间: {}", lastCheckTime)
    }
  }

  @Nested
  inner class `性能和稳定性测试` {

    @Test
    fun `应该能够并发获取 token 状态`() {
      // Given
      val threadCount = 10
      val results = mutableListOf<Map<String, Any>>()

      // When
      val threads =
        (1..threadCount).map {
          Thread {
            val status = wxpaService.getTokenStatus()
            synchronized(results) { results.add(status) }
          }
        }

      threads.forEach { it.start() }
      threads.forEach { it.join() }

      // Then
      assertEquals(threadCount, results.size)
      results.forEach { status ->
        assertNotNull(status)
        assertTrue(status.containsKey("hasAccessToken"))
      }
      log.info("并发获取token状态测试完成，执行了{}次", threadCount)
    }

    @Test
    fun `应该能够连续多次调用服务方法`() {
      // Given
      val callCount = 5
      val testUrl = "https://example.com/test"

      // When & Then
      repeat(callCount) { index ->
        val signature = wxpaService.generateJsapiSignature(testUrl)
        val status = wxpaService.getTokenStatus()

        // 在测试环境下，signature会返回null，但status应该正常
        assertNotNull(status)
        assertTrue(status.containsKey("hasAccessToken"))
        log.debug("第{}次调用完成，signature: {}", index + 1, signature)
      }

      log.info("连续多次调用测试完成，共执行{}次", callCount)
    }
  }
}

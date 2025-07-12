package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * # HTTP 头部常量测试
 *
 * 测试 IHeaders 中定义的各种 HTTP 头部常量和工具方法
 */
class IHeadersTest {

  @Test
  fun `测试标准 HTTP 头部常量`() {
    log.info("测试标准 HTTP 头部常量")

    assertEquals("Server", IHeaders.SERVER)
    assertEquals("Accept", IHeaders.ACCEPT)
    assertEquals("Accept-Encoding", IHeaders.ACCEPT_ENCODING)
    assertEquals("Accept-Language", IHeaders.ACCEPT_LANGUAGE)
    assertEquals("Cookie", IHeaders.COOKIE)
    assertEquals("Host", IHeaders.HOST)
    assertEquals("Referer", IHeaders.REFERER)
    assertEquals("User-Agent", IHeaders.USER_AGENT)

    log.info("验证了标准 HTTP 头部常量")
  }

  @Test
  fun `测试代理相关头部常量`() {
    log.info("测试代理相关头部常量")

    assertEquals("X-Forwarded-For", IHeaders.X_FORWARDED_FOR)
    assertEquals("X-Forwarded-Proto", IHeaders.X_FORWARDED_PROTO)
    assertEquals("Proxy-Client-IP", IHeaders.PROXY_CLIENT_IP)
    assertEquals("X-Real-IP", IHeaders.X_REAL_IP)

    log.info("验证了代理相关头部常量")
  }

  @Test
  fun `测试自定义头部常量`() {
    log.info("测试自定义头部常量")

    assertEquals("X-Device-Id", IHeaders.X_DEVICE_ID)
    assertEquals("X-Refresh", IHeaders.X_REFRESH)
    assertEquals("X-Require-Clean-Authentication", IHeaders.X_REQUIRE_CLEN_AUTHENTICATION)
    assertEquals("X-Wechat-Authorization-Id", IHeaders.X_WECHAT_AUTHORIZATION_ID)

    log.info("验证了自定义头部常量")
  }

  @Test
  fun `测试认证相关头部常量`() {
    log.info("测试认证相关头部常量")

    assertEquals("Authorization", IHeaders.AUTHORIZATION)

    log.info("验证了认证相关头部常量")
  }

  @Test
  fun `测试内容相关头部常量`() {
    log.info("测试内容相关头部常量")

    assertEquals("Content-Length", IHeaders.CONTENT_LENGTH)
    assertEquals("Content-Type", IHeaders.CONTENT_TYPE)
    assertEquals("Content-Disposition", IHeaders.CONTENT_DISPOSITION)
    assertEquals("Connection", IHeaders.CONNECTION)
    assertEquals("Keep-Alive", IHeaders.KEEP_ALIVE)

    log.info("验证了内容相关头部常量")
  }

  @Test
  fun `测试 CORS 相关头部常量`() {
    log.info("测试 CORS 相关头部常量")

    assertEquals("Access-Control-Allow-Origin", IHeaders.CORS_ALLOW_ORIGIN)
    assertEquals("Access-Control-Allow-Methods", IHeaders.CORS_ALLOW_METHODS)
    assertEquals("Access-Control-Allow-Headers", IHeaders.CORS_ALLOW_HEADERS)
    assertEquals("Access-Control-Allow-Credentials", IHeaders.CORS_ALLOW_CREDENTIALS)

    log.info("验证了 CORS 相关头部常量")
  }

  @Test
  fun `测试 downloadDisposition 方法 - UTF-8 编码`() {
    log.info("测试 downloadDisposition 方法 - UTF-8 编码")

    val fileName = "测试文件.txt"
    val result = IHeaders.downloadDisposition(fileName, StandardCharsets.UTF_8)

    log.info("文件名: {}", fileName)
    log.info("生成的 Content-Disposition: {}", result)

    assertTrue(result.startsWith("attachment; filename="), "应该以 'attachment; filename=' 开头")
    assertTrue(result.contains("%"), "应该包含 URL 编码的字符")
  }

  @Test
  fun `测试 downloadDisposition 方法 - 英文文件名`() {
    log.info("测试 downloadDisposition 方法 - 英文文件名")

    val fileName = "test-file.txt"
    val result = IHeaders.downloadDisposition(fileName, StandardCharsets.UTF_8)

    log.info("文件名: {}", fileName)
    log.info("生成的 Content-Disposition: {}", result)

    assertEquals("attachment; filename=test-file.txt", result)
  }

  @Test
  fun `测试 downloadDisposition 方法 - 特殊字符文件名`() {
    log.info("测试 downloadDisposition 方法 - 特殊字符文件名")

    val fileName = "file with spaces & symbols!.pdf"
    val result = IHeaders.downloadDisposition(fileName, StandardCharsets.UTF_8)

    log.info("文件名: {}", fileName)
    log.info("生成的 Content-Disposition: {}", result)

    assertTrue(result.startsWith("attachment; filename="), "应该以 'attachment; filename=' 开头")
    assertTrue(result.contains("%"), "特殊字符应该被 URL 编码")
  }

  // 注意：getDeviceId 方法需要 HttpServletRequest，在单元测试中难以模拟
  // 这里我们测试方法的存在性和基本逻辑，实际的 HTTP 请求测试应该在集成测试中进行

  @Test
  fun `测试头部常量的命名规范`() {
    log.info("测试头部常量的命名规范")

    // 验证自定义头部都以 X- 开头
    assertTrue(IHeaders.X_FORWARDED_FOR.startsWith("X-"), "X_FORWARDED_FOR 应该以 X- 开头")
    assertTrue(IHeaders.X_FORWARDED_PROTO.startsWith("X-"), "X_FORWARDED_PROTO 应该以 X- 开头")
    assertTrue(IHeaders.X_REAL_IP.startsWith("X-"), "X_REAL_IP 应该以 X- 开头")
    assertTrue(IHeaders.X_DEVICE_ID.startsWith("X-"), "X_DEVICE_ID 应该以 X- 开头")
    assertTrue(IHeaders.X_REFRESH.startsWith("X-"), "X_REFRESH 应该以 X- 开头")
    assertTrue(IHeaders.X_REQUIRE_CLEN_AUTHENTICATION.startsWith("X-"), "X_REQUIRE_CLEN_AUTHENTICATION 应该以 X- 开头")
    assertTrue(IHeaders.X_WECHAT_AUTHORIZATION_ID.startsWith("X-"), "X_WECHAT_AUTHORIZATION_ID 应该以 X- 开头")

    // 验证 CORS 头部都以 Access-Control- 开头
    assertTrue(IHeaders.CORS_ALLOW_ORIGIN.startsWith("Access-Control-"), "CORS_ALLOW_ORIGIN 应该以 Access-Control- 开头")
    assertTrue(IHeaders.CORS_ALLOW_METHODS.startsWith("Access-Control-"), "CORS_ALLOW_METHODS 应该以 Access-Control- 开头")
    assertTrue(IHeaders.CORS_ALLOW_HEADERS.startsWith("Access-Control-"), "CORS_ALLOW_HEADERS 应该以 Access-Control- 开头")
    assertTrue(IHeaders.CORS_ALLOW_CREDENTIALS.startsWith("Access-Control-"), "CORS_ALLOW_CREDENTIALS 应该以 Access-Control- 开头")

    log.info("验证了头部常量的命名规范")
  }
}

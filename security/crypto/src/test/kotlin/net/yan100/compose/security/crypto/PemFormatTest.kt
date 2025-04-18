package net.yan100.compose.security.crypto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * PemFormat 的单元测试
 */
class PemFormatTest {

  companion object {
    private val VALID_PEM = """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEowIBAAKCAQEAvRKXmC7E8y0F1olUhrH8YuSVfaYhJ1ySqInrROcbXXXXXXXX
            q4t5iHJjXXXXXXXXMpzXXXXXXXXq8XXXXXXXX+XXXXXXXXrpXXXXXXXX1XXXXXXXX
            -----END RSA PRIVATE KEY-----
        """.trimIndent()

    private const val VALID_PEM_SCHEMA = "RSA PRIVATE KEY"
    private const val VALID_PEM_CONTENT =
      "MIIEowIBAAKCAQEAvRKXmC7E8y0F1olUhrH8YuSVfaYhJ1ySqInrROcbXXXXXXXXq4t5iHJjXXXXXXXXMpzXXXXXXXXq8XXXXXXXX+XXXXXXXXrpXXXXXXXX1XXXXXXXX"

    private fun generateTestKeyPair(): KeyPair =
      KeyPairGenerator.getInstance("RSA").apply {
        initialize(2048)
      }.generateKeyPair()

    @JvmStatic
    fun invalidPemProvider(): Stream<Arguments> = Stream.of(
      Arguments.of("不匹配的头尾类型", "-----BEGIN TEST-----\nInvalid\n-----END DIFFERENT-----"),
      Arguments.of("空类型", "-----BEGIN -----\nContent\n-----END -----"),
      Arguments.of("无头尾标记", "Content"),
      Arguments.of("空字符串", "")
    )

    @JvmStatic
    fun keyTypeProvider(): Stream<Arguments> = Stream.of(
      Arguments.of("普通类型", "TEST KEY", "-----BEGIN TEST KEY-----", "-----END TEST KEY-----"),
      Arguments.of("带空格类型", "  TEST KEY  ", "-----BEGIN TEST KEY-----", "-----END TEST KEY-----"),
      Arguments.of("空类型", null, "-----BEGIN -----", "-----END -----"),
      Arguments.of("特殊字符类型", "RSA/ECB/PKCS1", "-----BEGIN RSA/ECB/PKCS1-----", "-----END RSA/ECB/PKCS1-----")
    )
  }

  @Test
  fun `测试解析有效的 PEM 格式字符串`() {
    val pemFormat = PemFormat.parse(VALID_PEM)

    assertEquals(VALID_PEM_SCHEMA, pemFormat.schema)
    assertEquals(VALID_PEM_CONTENT, pemFormat.content)
  }

  @Test
  fun `测试从密钥对象创建 PEM 格式字符串`() {
    val keyPair = generateTestKeyPair()
    val pem = PemFormat[keyPair.private]

    assertNotNull(pem)
    assert(pem.startsWith("-----BEGIN RSA PRIVATE KEY-----"))
    assert(pem.endsWith("-----END RSA PRIVATE KEY-----"))
  }

  @Test
  fun `测试从 Base64 字符串创建 PEM 格式字符串`() {
    val base64 = "SGVsbG8gV29ybGQ=" // "Hello World" in Base64
    val keyType = "TEST KEY"
    val pem = PemFormat[base64, keyType]

    assert(pem.startsWith("-----BEGIN TEST KEY-----"))
    assert(pem.contains(base64))
    assert(pem.endsWith("-----END TEST KEY-----"))
  }

  @ParameterizedTest(name = "测试解析无效的 PEM 格式字符串 - {0}")
  @MethodSource("invalidPemProvider")
  fun `测试解析无效的 PEM 格式字符串`(testCase: String, invalidPem: String) {
    assertThrows<IllegalArgumentException> {
      PemFormat.parse(invalidPem)
    }
  }

  @Test
  fun `测试 PEM 内容格式化`() {
    val longBase64 = "A".repeat(100)
    val pem = PemFormat[longBase64, "TEST"]

    // 验证每行长度不超过64个字符
    pem.lines().forEach { line ->
      if (line != "-----BEGIN TEST-----" &&
        line != "-----END TEST-----" &&
        line.isNotBlank()
      ) {
        assert(line.length <= 64) { "行长度超过64个字符: $line" }
      }
    }
  }

  @ParameterizedTest(name = "测试密钥类型「{0}」")
  @MethodSource("keyTypeProvider")
  fun `测试不同密钥类型`(
    testCase: String,
    keyType: String?,
    expectedBegin: String,
    expectedEnd: String,
  ) {
    val base64 = "SGVsbG8gV29ybGQ="
    val pem = PemFormat[base64, keyType]

    assert(pem.startsWith(expectedBegin)) { "期望以 $expectedBegin 开头，实际为：$pem" }
    assert(pem.endsWith(expectedEnd)) { "期望以 $expectedEnd 结尾，实际为：$pem" }
  }

  @Test
  fun `测试空 Base64 内容`() {
    assertThrows<IllegalArgumentException>("Base64 内容不能为空") {
      PemFormat["", "TEST"]
    }
  }

  @ParameterizedTest(name = "测试换行符「{0}」")
  @ValueSource(strings = ["\n", "\r", "\r\n"])
  fun `测试不同换行符处理`(lineEnding: String) {
    val pemWithDifferentLineEndings = """
            -----BEGIN TEST-----${lineEnding}
            SGVsbG8gV29ybGQ=${lineEnding}
            -----END TEST-----
        """.trimIndent()

    val pemFormat = PemFormat.parse(pemWithDifferentLineEndings)
    assertEquals("TEST", pemFormat.schema)
    assertEquals("SGVsbG8gV29ybGQ=", pemFormat.content)
  }
}

package io.github.truenine.composeserver.oss.volcengine

import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

/** Volcengine TOS 配置属性测试 */
class VolcengineTosPropertiesTest {

  @Nested
  inner class DefaultValues {

    @Test
    fun `测试默认值设置`() {
      val properties = VolcengineTosProperties()

      // Authentication
      assertNull(properties.accessKey)
      assertNull(properties.secretKey)
      assertNull(properties.sessionToken)
      assertNull(properties.endpoint)
      assertNull(properties.region)
      assertNull(properties.exposedBaseUrl)

      // Network settings
      assertTrue(properties.enableSsl)
      assertEquals(10_000, properties.connectTimeoutMills)
      assertEquals(60_000, properties.readTimeoutMills)
      assertEquals(60_000, properties.writeTimeoutMills)
      assertEquals(60_000, properties.idleConnectionTimeMills)

      // Connection pool
      assertEquals(1024, properties.maxConnections)
      assertEquals(3, properties.maxRetryCount)
      assertEquals(5, properties.dnsCacheTimeMinutes)

      // Features
      assertTrue(properties.enableCrc)
      assertTrue(properties.enableVerifySSL)
      assertTrue(properties.clientAutoRecognizeContentType)
      assertFalse(properties.enableLogging)

      // Domain
      assertNull(properties.customDomain)
      assertFalse(properties.isCustomDomain)

      // User-Agent
      assertEquals("ComposeServer", properties.userAgentProductName)
      assertEquals("TOS-Client", properties.userAgentSoftName)
      assertEquals("1.0.0", properties.userAgentSoftVersion)
      assertTrue(properties.userAgentCustomizedKeyValues.isEmpty())

      // Proxy
      assertNull(properties.proxyHost)
      assertEquals(0, properties.proxyPort)
      assertNull(properties.proxyUserName)
      assertNull(properties.proxyPassword)
    }
  }

  @Nested
  inner class PropertyAssignment {

    @Test
    fun `测试属性赋值`() {
      val customKeyValues = mapOf("env" to "test", "service" to "oss")
      val properties =
        VolcengineTosProperties(
          accessKey = "test-access-key",
          secretKey = "test-secret-key",
          sessionToken = "test-session-token",
          endpoint = "https://tos-s3-cn-beijing.volces.com",
          region = "cn-beijing",
          exposedBaseUrl = "https://cdn.example.com",
          enableSsl = false,
          connectTimeoutMills = 15_000,
          readTimeoutMills = 90_000,
          writeTimeoutMills = 90_000,
          idleConnectionTimeMills = 30_000,
          maxConnections = 512,
          maxRetryCount = 5,
          dnsCacheTimeMinutes = 10,
          enableCrc = false,
          enableVerifySSL = false,
          clientAutoRecognizeContentType = false,
          enableLogging = true,
          customDomain = "custom.example.com",
          isCustomDomain = true,
          userAgentProductName = "TestApp",
          userAgentSoftName = "TestClient",
          userAgentSoftVersion = "2.0.0",
          userAgentCustomizedKeyValues = customKeyValues,
          proxyHost = "proxy.example.com",
          proxyPort = 8080,
          proxyUserName = "proxyuser",
          proxyPassword = "proxypass",
        )

      assertEquals("test-access-key", properties.accessKey)
      assertEquals("test-secret-key", properties.secretKey)
      assertEquals("test-session-token", properties.sessionToken)
      assertEquals("https://tos-s3-cn-beijing.volces.com", properties.endpoint)
      assertEquals("cn-beijing", properties.region)
      assertEquals("https://cdn.example.com", properties.exposedBaseUrl)
      assertFalse(properties.enableSsl)
      assertEquals(15_000, properties.connectTimeoutMills)
      assertEquals(90_000, properties.readTimeoutMills)
      assertEquals(90_000, properties.writeTimeoutMills)
      assertEquals(30_000, properties.idleConnectionTimeMills)
      assertEquals(512, properties.maxConnections)
      assertEquals(5, properties.maxRetryCount)
      assertEquals(10, properties.dnsCacheTimeMinutes)
      assertFalse(properties.enableCrc)
      assertFalse(properties.enableVerifySSL)
      assertFalse(properties.clientAutoRecognizeContentType)
      assertTrue(properties.enableLogging)
      assertEquals("custom.example.com", properties.customDomain)
      assertTrue(properties.isCustomDomain)
      assertEquals("TestApp", properties.userAgentProductName)
      assertEquals("TestClient", properties.userAgentSoftName)
      assertEquals("2.0.0", properties.userAgentSoftVersion)
      assertEquals(customKeyValues, properties.userAgentCustomizedKeyValues)
      assertEquals("proxy.example.com", properties.proxyHost)
      assertEquals(8080, properties.proxyPort)
      assertEquals("proxyuser", properties.proxyUserName)
      assertEquals("proxypass", properties.proxyPassword)
    }
  }

  @Nested
  inner class ValidationTests {

    @Test
    fun `测试有效配置验证通过`() {
      val properties =
        VolcengineTosProperties(
          connectTimeoutMills = 10_000,
          readTimeoutMills = 60_000,
          writeTimeoutMills = 60_000,
          idleConnectionTimeMills = 30_000,
          maxConnections = 100,
          maxRetryCount = 3,
          dnsCacheTimeMinutes = 5,
        )

      // Should not throw exception
      assertDoesNotThrow { properties.validate() }
    }

    @Test
    fun `测试无效超时配置验证失败`() {
      assertThrows<IllegalArgumentException> { VolcengineTosProperties(connectTimeoutMills = -1).validate() }

      assertThrows<IllegalArgumentException> { VolcengineTosProperties(readTimeoutMills = 0).validate() }

      assertThrows<IllegalArgumentException> { VolcengineTosProperties(writeTimeoutMills = -100).validate() }

      assertThrows<IllegalArgumentException> { VolcengineTosProperties(idleConnectionTimeMills = 0).validate() }
    }

    @Test
    fun `测试无效连接池配置验证失败`() {
      assertThrows<IllegalArgumentException> { VolcengineTosProperties(maxConnections = 0).validate() }

      assertThrows<IllegalArgumentException> { VolcengineTosProperties(maxRetryCount = -1).validate() }

      assertThrows<IllegalArgumentException> { VolcengineTosProperties(dnsCacheTimeMinutes = -1).validate() }
    }

    @Test
    fun `测试代理配置验证`() {
      // Valid proxy configuration
      assertDoesNotThrow { VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 8080).validate() }

      // Invalid proxy configuration - host without port
      assertThrows<IllegalArgumentException> { VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 0).validate() }
    }
  }

  @Nested
  inner class HelperMethods {

    @Test
    fun `测试getEffectiveEndpoint方法`() {
      // Null endpoint
      val nullEndpointProps = VolcengineTosProperties(endpoint = null)
      assertNull(nullEndpointProps.getEffectiveEndpoint())

      // Endpoint with protocol
      val httpsProps = VolcengineTosProperties(endpoint = "https://tos.example.com")
      assertEquals("https://tos.example.com", httpsProps.getEffectiveEndpoint())

      val httpProps = VolcengineTosProperties(endpoint = "http://tos.example.com")
      assertEquals("http://tos.example.com", httpProps.getEffectiveEndpoint())

      // Endpoint without protocol - SSL enabled
      val sslProps = VolcengineTosProperties(endpoint = "tos.example.com", enableSsl = true)
      assertEquals("https://tos.example.com", sslProps.getEffectiveEndpoint())

      // Endpoint without protocol - SSL disabled
      val noSslProps = VolcengineTosProperties(endpoint = "tos.example.com", enableSsl = false)
      assertEquals("http://tos.example.com", noSslProps.getEffectiveEndpoint())
    }

    @Test
    fun `测试hasProxyConfiguration方法`() {
      // No proxy
      val noProxyProps = VolcengineTosProperties()
      assertFalse(noProxyProps.hasProxyConfiguration())

      // Valid proxy
      val validProxyProps = VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 8080)
      assertTrue(validProxyProps.hasProxyConfiguration())

      // Invalid proxy - no host
      val noHostProps = VolcengineTosProperties(proxyPort = 8080)
      assertFalse(noHostProps.hasProxyConfiguration())

      // Invalid proxy - no port
      val noPortProps = VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 0)
      assertFalse(noPortProps.hasProxyConfiguration())
    }

    @Test
    fun `测试hasProxyAuthentication方法`() {
      // No proxy
      val noProxyProps = VolcengineTosProperties()
      assertFalse(noProxyProps.hasProxyAuthentication())

      // Proxy without auth
      val noAuthProps = VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 8080)
      assertFalse(noAuthProps.hasProxyAuthentication())

      // Proxy with auth
      val authProps = VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 8080, proxyUserName = "user", proxyPassword = "pass")
      assertTrue(authProps.hasProxyAuthentication())

      // Proxy with incomplete auth
      val incompleteAuthProps = VolcengineTosProperties(proxyHost = "proxy.example.com", proxyPort = 8080, proxyUserName = "user")
      assertFalse(incompleteAuthProps.hasProxyAuthentication())
    }
  }

  @Nested
  inner class BooleanProperties {

    @Test
    fun `测试布尔属性默认值`() {
      val properties = VolcengineTosProperties()

      assertTrue(properties.enableSsl)
      assertTrue(properties.enableCrc)
      assertTrue(properties.enableVerifySSL)
      assertTrue(properties.clientAutoRecognizeContentType)
      assertFalse(properties.enableLogging)
      assertFalse(properties.isCustomDomain)
    }

    @Test
    fun `测试布尔属性设置`() {
      val properties =
        VolcengineTosProperties(
          enableSsl = false,
          enableCrc = false,
          enableVerifySSL = false,
          clientAutoRecognizeContentType = false,
          enableLogging = true,
          isCustomDomain = true,
        )

      assertFalse(properties.enableSsl)
      assertFalse(properties.enableCrc)
      assertFalse(properties.enableVerifySSL)
      assertFalse(properties.clientAutoRecognizeContentType)
      assertTrue(properties.enableLogging)
      assertTrue(properties.isCustomDomain)
    }
  }

  @Nested
  inner class TimeoutProperties {

    @Test
    fun `测试超时属性边界值`() {
      val timeoutValues = listOf(1, 1000, 5000, 10_000, 30_000, 60_000, 120_000)

      timeoutValues.forEach { value ->
        val properties =
          VolcengineTosProperties(connectTimeoutMills = value, readTimeoutMills = value, writeTimeoutMills = value, idleConnectionTimeMills = value)

        assertEquals(value, properties.connectTimeoutMills)
        assertEquals(value, properties.readTimeoutMills)
        assertEquals(value, properties.writeTimeoutMills)
        assertEquals(value, properties.idleConnectionTimeMills)
      }
    }
  }

  @Nested
  inner class IntegerProperties {

    @Test
    fun `测试整数属性边界值`() {
      // 测试连接数
      val connectionValues = listOf(1, 10, 50, 100, 500, 1024, 2048)
      connectionValues.forEach { value ->
        val properties = VolcengineTosProperties(maxConnections = value)
        assertEquals(value, properties.maxConnections)
      }

      // 测试重试次数
      val retryValues = listOf(0, 1, 3, 5, 10)
      retryValues.forEach { value ->
        val properties = VolcengineTosProperties(maxRetryCount = value)
        assertEquals(value, properties.maxRetryCount)
      }

      // 测试DNS缓存时间
      val cacheValues = listOf(0, 1, 5, 10, 30, 60)
      cacheValues.forEach { value ->
        val properties = VolcengineTosProperties(dnsCacheTimeMinutes = value)
        assertEquals(value, properties.dnsCacheTimeMinutes)
      }
    }
  }

  @Nested
  inner class UserAgentProperties {

    @Test
    fun `测试User-Agent属性`() {
      val customKeyValues = mapOf("environment" to "production", "service" to "oss-service", "version" to "1.2.3")

      val properties =
        VolcengineTosProperties(
          userAgentProductName = "MyApp",
          userAgentSoftName = "MyClient",
          userAgentSoftVersion = "2.1.0",
          userAgentCustomizedKeyValues = customKeyValues,
        )

      assertEquals("MyApp", properties.userAgentProductName)
      assertEquals("MyClient", properties.userAgentSoftName)
      assertEquals("2.1.0", properties.userAgentSoftVersion)
      assertEquals(customKeyValues, properties.userAgentCustomizedKeyValues)
    }
  }

  @Nested
  inner class ToStringMethod {

    @Test
    fun `测试toString方法不暴露敏感信息`() {
      val properties =
        VolcengineTosProperties(accessKey = "very-secret-access-key", secretKey = "very-secret-secret-key", proxyPassword = "secret-proxy-password")

      val toString = properties.toString()

      // Should contain masked access key
      assertTrue(toString.contains("very***"))

      // Should not contain full secret key or proxy password
      assertFalse(toString.contains("very-secret-secret-key"))
      assertFalse(toString.contains("secret-proxy-password"))

      // Should contain configuration summary
      assertTrue(toString.contains("VolcengineTosProperties"))
      assertTrue(toString.contains("enableSsl=true"))
      assertTrue(toString.contains("maxConnections=1024"))
    }
  }
}

package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.depend.springdocopenapi.properties.JwtHeaderInfoProperties
import io.github.truenine.composeserver.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import io.github.truenine.composeserver.depend.springdocopenapi.properties.SwaggerDescInfo
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [TestApplication::class])
@TestPropertySource(
  properties =
    [
      "compose.depend.springdoc-open-api.group=custom-group",
      "compose.depend.springdoc-open-api.enable-jwt-header=false",
      "compose.depend.springdoc-open-api.scan-packages[0]=com.example.package1",
      "compose.depend.springdoc-open-api.scan-packages[1]=com.example.package2",
      "compose.depend.springdoc-open-api.scan-url-patterns[0]=/api/v1/**",
      "compose.depend.springdoc-open-api.scan-url-patterns[1]=/api/v2/**",
      "compose.depend.springdoc-open-api.author-info.title=Custom API",
      "compose.depend.springdoc-open-api.author-info.version=3.1.0",
      "compose.depend.springdoc-open-api.author-info.description=Custom API Description",
      "compose.depend.springdoc-open-api.author-info.location=https://example.com",
      "compose.depend.springdoc-open-api.author-info.license=Apache-2.0",
      "compose.depend.springdoc-open-api.author-info.license-url=https://www.apache.org/licenses/LICENSE-2.0",
      "compose.depend.springdoc-open-api.jwt-header-info.auth-token-name=Authorization",
      "compose.depend.springdoc-open-api.jwt-header-info.refresh-token-name=Refresh-Token",
    ]
)
@DisplayName("配置属性测试")
class PropertiesTest {

  @Resource lateinit var properties: SpringdocOpenApiProperties

  @Test
  @DisplayName("测试基础配置属性")
  fun `should load basic configuration properties correctly`() {
    with(properties) {
      assertEquals("custom-group", group, "Group should match configuration")
      assertFalse(enableJwtHeader, "JWT header should be disabled")

      // 注意：OpenApiDocConfig 会自动添加 "net.yan100.compose" 包，所以总数会是 3
      assertTrue(scanPackages.size >= 2, "Should have at least 2 scan packages")
      assertTrue(scanPackages.contains("com.example.package1"), "Should contain package1")
      assertTrue(scanPackages.contains("com.example.package2"), "Should contain package2")

      assertEquals(2, scanUrlPatterns.size, "Should have 2 URL patterns")
      assertTrue(scanUrlPatterns.contains("/api/v1/**"), "Should contain v1 pattern")
      assertTrue(scanUrlPatterns.contains("/api/v2/**"), "Should contain v2 pattern")
    }
  }

  @Test
  @DisplayName("测试作者信息配置")
  fun `should load author info configuration correctly`() {
    with(properties.authorInfo) {
      assertEquals("Custom API", title, "Title should match configuration")
      assertEquals("3.1.0", version, "Version should match configuration")
      assertEquals("Custom API Description", description, "Description should match configuration")
      assertEquals("https://example.com", location, "Location should match configuration")
      assertEquals("Apache-2.0", license, "License should match configuration")
      assertEquals("https://www.apache.org/licenses/LICENSE-2.0", licenseUrl, "License URL should match configuration")
    }
  }

  @Test
  @DisplayName("测试 JWT 头信息配置")
  fun `should load JWT header info configuration correctly`() {
    with(properties.jwtHeaderInfo) {
      assertEquals("Authorization", authTokenName, "Auth token name should match configuration")
      assertEquals("Refresh-Token", refreshTokenName, "Refresh token name should match configuration")
    }
  }

  @Test
  @DisplayName("测试嵌套配置对象")
  fun `should create nested configuration objects correctly`() {
    assertNotNull(properties.authorInfo, "Author info should not be null")
    assertNotNull(properties.jwtHeaderInfo, "JWT header info should not be null")

    assertTrue(properties.authorInfo is SwaggerDescInfo, "Author info should be SwaggerDescInfo type")
    assertTrue(properties.jwtHeaderInfo is JwtHeaderInfoProperties, "JWT header info should be JwtHeaderInfoProperties type")
  }
}

@SpringBootTest(classes = [TestApplication::class])
@DisplayName("默认配置属性测试")
class DefaultPropertiesTest {

  @Resource lateinit var properties: SpringdocOpenApiProperties

  @Test
  @DisplayName("测试默认配置值")
  fun `should use default configuration values when not specified`() {
    with(properties) {
      assertEquals("default", group, "Default group should be 'default'")
      assertFalse(enableJwtHeader, "JWT header should be disabled by default")

      // OpenApiDocConfig 会自动添加 "net.yan100.compose" 包，所以不会为空
      assertTrue(scanPackages.size >= 1, "Scan packages should contain at least the auto-added packages")

      assertEquals(1, scanUrlPatterns.size, "Should have default URL pattern")
      assertTrue(scanUrlPatterns.contains("/**"), "Should contain default pattern")
    }
  }

  @Test
  @DisplayName("测试默认作者信息")
  fun `should use default author info when not specified`() {
    with(properties.authorInfo) {
      assertNotNull(title, "Title should have default value")
      assertNotNull(version, "Version should have default value")
      assertNotNull(description, "Description should have default value")
    }
  }

  @Test
  @DisplayName("测试默认 JWT 头信息")
  fun `should use default JWT header info when not specified`() {
    with(properties.jwtHeaderInfo) {
      assertNotNull(authTokenName, "Auth token name should have default value")
      assertNotNull(refreshTokenName, "Refresh token name should have default value")
    }
  }
}

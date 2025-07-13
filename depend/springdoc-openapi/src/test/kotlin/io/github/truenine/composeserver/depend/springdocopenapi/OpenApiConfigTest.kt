package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.depend.springdocopenapi.autoconfig.OpenApiDocConfig
import io.github.truenine.composeserver.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import io.swagger.v3.oas.models.OpenAPI
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [TestApplication::class])
@TestPropertySource(
  properties =
    [
      "compose.depend.springdoc-open-api.group=test-api",
      "compose.depend.springdoc-open-api.enable-jwt-header=true",
      "compose.depend.springdoc-open-api.scan-packages[0]=io.github.truenine.composeserver.depend.springdocopenapi",
      "compose.depend.springdoc-open-api.scan-url-patterns[0]=/test/**",
      "compose.depend.springdoc-open-api.author-info.title=Test API Documentation",
      "compose.depend.springdoc-open-api.author-info.version=2.0.0",
      "compose.depend.springdoc-open-api.author-info.description=This is a test API for SpringDoc OpenAPI integration",
      "compose.depend.springdoc-open-api.author-info.license=MIT",
      "compose.depend.springdoc-open-api.author-info.license-url=https://opensource.org/licenses/MIT",
      "compose.depend.springdoc-open-api.jwt-header-info.auth-token-name=X-Auth-Token",
      "compose.depend.springdoc-open-api.jwt-header-info.refresh-token-name=X-Refresh-Token",
    ]
)
@DisplayName("OpenAPI 配置测试")
class OpenApiConfigTest {

  @Resource lateinit var openApiDocConfig: OpenApiDocConfig

  @Resource lateinit var springdocOpenApiProperties: SpringdocOpenApiProperties

  @Resource lateinit var groupedOpenApi: GroupedOpenApi

  @Resource lateinit var customOpenApi: OpenAPI

  @Test
  @DisplayName("测试 OpenAPI 配置 Bean 是否正确创建")
  fun `should create OpenAPI config beans correctly`() {
    assertNotNull(openApiDocConfig, "OpenApiDocConfig bean should be created")
    assertNotNull(springdocOpenApiProperties, "SpringdocOpenApiProperties bean should be created")
    assertNotNull(groupedOpenApi, "GroupedOpenApi bean should be created")
    assertNotNull(customOpenApi, "Custom OpenAPI bean should be created")
  }

  @Test
  @DisplayName("测试配置属性是否正确加载")
  fun `should load configuration properties correctly`() {
    with(springdocOpenApiProperties) {
      assertEquals("test-api", group, "Group name should match configuration")
      assertTrue(enableJwtHeader, "JWT header should be enabled")
      assertTrue(scanPackages.contains("io.github.truenine.composeserver.depend.springdocopenapi"), "Scan packages should contain test package")
      assertTrue(scanUrlPatterns.contains("/test/**"), "Scan URL patterns should contain test pattern")

      with(authorInfo) {
        assertEquals("Test API Documentation", title, "Title should match configuration")
        assertEquals("2.0.0", version, "Version should match configuration")
        assertEquals("This is a test API for SpringDoc OpenAPI integration", description, "Description should match configuration")
        assertEquals("MIT", license, "License should match configuration")
        assertEquals("https://opensource.org/licenses/MIT", licenseUrl, "License URL should match configuration")
      }

      with(jwtHeaderInfo) {
        assertEquals("X-Auth-Token", authTokenName, "Auth token name should match configuration")
        assertEquals("X-Refresh-Token", refreshTokenName, "Refresh token name should match configuration")
      }
    }
  }

  @Test
  @DisplayName("测试 GroupedOpenApi 配置")
  fun `should configure GroupedOpenApi correctly`() {
    assertEquals("test-api", groupedOpenApi.group, "GroupedOpenApi group should match configuration")
    assertNotNull(groupedOpenApi.pathsToMatch, "Paths to match should be configured")
    assertNotNull(groupedOpenApi.packagesToScan, "Packages to scan should be configured")
  }

  @Test
  @DisplayName("测试自定义 OpenAPI 配置")
  fun `should configure custom OpenAPI correctly`() {
    with(customOpenApi.info) {
      assertEquals("Test API Documentation", title, "OpenAPI title should match configuration")
      assertEquals("2.0.0", version, "OpenAPI version should match configuration")
      assertEquals("This is a test API for SpringDoc OpenAPI integration", description, "OpenAPI description should match configuration")

      assertNotNull(license, "License should be configured")
      assertEquals("MIT", license.name, "License name should match configuration")
      assertEquals("https://opensource.org/licenses/MIT", license.url, "License URL should match configuration")
    }
  }
}

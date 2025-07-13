package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.depend.springdocopenapi.autoconfig.AutoConfigEntrance
import io.github.truenine.composeserver.depend.springdocopenapi.autoconfig.OpenApiDocConfig
import io.github.truenine.composeserver.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import io.swagger.v3.oas.models.OpenAPI
import jakarta.annotation.Resource
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [TestApplication::class])
@TestPropertySource(properties = ["compose.depend.springdoc-open-api.group=auto-config-test", "compose.depend.springdoc-open-api.enable-jwt-header=true"])
@DisplayName("自动配置测试")
class AutoConfigurationTest {

  @Resource lateinit var applicationContext: ApplicationContext

  @Test
  @DisplayName("测试自动配置入口类是否正确加载")
  fun `should load auto configuration entrance correctly`() {
    val autoConfigEntrance = applicationContext.getBean(AutoConfigEntrance::class.java)
    assertNotNull(autoConfigEntrance, "AutoConfigEntrance should be loaded")
  }

  @Test
  @DisplayName("测试配置属性 Bean 是否正确创建")
  fun `should create configuration properties bean correctly`() {
    val properties = applicationContext.getBean(SpringdocOpenApiProperties::class.java)
    assertNotNull(properties, "SpringdocOpenApiProperties should be created")
  }

  @Test
  @DisplayName("测试 OpenAPI 配置 Bean 是否正确创建")
  fun `should create openapi config bean correctly`() {
    val openApiDocConfig = applicationContext.getBean(OpenApiDocConfig::class.java)
    assertNotNull(openApiDocConfig, "OpenApiDocConfig should be created")
  }

  @Test
  @DisplayName("测试 GroupedOpenApi Bean 是否正确创建")
  fun `should create grouped openapi bean correctly`() {
    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    assertNotNull(groupedOpenApi, "GroupedOpenApi should be created")
  }

  @Test
  @DisplayName("测试自定义 OpenAPI Bean 是否正确创建")
  fun `should create custom openapi bean correctly`() {
    val customOpenApi = applicationContext.getBean(OpenAPI::class.java)
    assertNotNull(customOpenApi, "Custom OpenAPI should be created")
  }

  @Test
  @DisplayName("测试所有必需的 Bean 是否都存在")
  fun `should have all required beans in application context`() {
    val beanNames = applicationContext.beanDefinitionNames.toList()

    // 检查关键 Bean 是否存在
    assertTrue(beanNames.any { it.contains("autoConfigEntrance") || it.contains("AutoConfigEntrance") }, "AutoConfigEntrance bean should exist")
    assertTrue(
      beanNames.any { it.contains("springdocOpenApiProperties") || it.contains("SpringdocOpenApiProperties") },
      "SpringdocOpenApiProperties bean should exist",
    )
    assertTrue(beanNames.any { it.contains("openApiDocConfig") || it.contains("OpenApiDocConfig") }, "OpenApiDocConfig bean should exist")
  }

  @Test
  @DisplayName("测试 Bean 的依赖关系")
  fun `should have correct bean dependencies`() {
    val openApiDocConfig = applicationContext.getBean(OpenApiDocConfig::class.java)
    val properties = applicationContext.getBean(SpringdocOpenApiProperties::class.java)

    assertNotNull(openApiDocConfig, "OpenApiDocConfig should be available")
    assertNotNull(properties, "SpringdocOpenApiProperties should be available")

    // 验证配置是否正确应用
    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    val customOpenApi = applicationContext.getBean(OpenAPI::class.java)

    assertNotNull(groupedOpenApi, "GroupedOpenApi should be created with dependencies")
    assertNotNull(customOpenApi, "Custom OpenAPI should be created with dependencies")
  }
}

@SpringBootTest(classes = [TestApplication::class])
@TestPropertySource(properties = ["compose.depend.springdoc-open-api.enable-jwt-header=false"])
@DisplayName("条件配置测试")
class ConditionalConfigurationTest {

  @Resource lateinit var applicationContext: ApplicationContext

  @Test
  @DisplayName("测试禁用 JWT 头时的配置")
  fun `should configure correctly when jwt header is disabled`() {
    val properties = applicationContext.getBean(SpringdocOpenApiProperties::class.java)
    assertNotNull(properties, "Properties should still be created")

    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    assertNotNull(groupedOpenApi, "GroupedOpenApi should still be created")
  }

  @Test
  @DisplayName("测试 Web 应用条件配置")
  fun `should configure correctly for web application`() {
    // 验证 @ConditionalOnWebApplication 注解的效果
    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    assertNotNull(groupedOpenApi, "GroupedOpenApi should be created in web application context")
  }
}

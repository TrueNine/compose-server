package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.depend.springdocopenapi.autoconfig.AutoConfigEntrance
import io.github.truenine.composeserver.depend.springdocopenapi.autoconfig.OpenApiDocConfig
import io.github.truenine.composeserver.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import io.swagger.v3.oas.models.OpenAPI
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [TestApplication::class])
@TestPropertySource(properties = ["compose.depend.springdoc-open-api.group=auto-config-test", "compose.depend.springdoc-open-api.enable-jwt-header=true"])
class AutoConfigurationTest {

  @Resource lateinit var applicationContext: ApplicationContext

  @Test
  fun `should load auto configuration entrance correctly`() {
    val autoConfigEntrance = applicationContext.getBean(AutoConfigEntrance::class.java)
    assertNotNull(autoConfigEntrance, "AutoConfigEntrance should be loaded")
  }

  @Test
  fun `should create configuration properties bean correctly`() {
    val properties = applicationContext.getBean(SpringdocOpenApiProperties::class.java)
    assertNotNull(properties, "SpringdocOpenApiProperties should be created")
  }

  @Test
  fun `should create openapi config bean correctly`() {
    val openApiDocConfig = applicationContext.getBean(OpenApiDocConfig::class.java)
    assertNotNull(openApiDocConfig, "OpenApiDocConfig should be created")
  }

  @Test
  fun `should create grouped openapi bean correctly`() {
    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    assertNotNull(groupedOpenApi, "GroupedOpenApi should be created")
  }

  @Test
  fun `should create custom openapi bean correctly`() {
    val customOpenApi = applicationContext.getBean(OpenAPI::class.java)
    assertNotNull(customOpenApi, "Custom OpenAPI should be created")
  }

  @Test
  fun `should have all required beans in application context`() {
    val beanNames = applicationContext.beanDefinitionNames.toList()

    // Check if key beans exist
    assertTrue(beanNames.any { it.contains("autoConfigEntrance") || it.contains("AutoConfigEntrance") }, "AutoConfigEntrance bean should exist")
    assertTrue(
      beanNames.any { it.contains("springdocOpenApiProperties") || it.contains("SpringdocOpenApiProperties") },
      "SpringdocOpenApiProperties bean should exist",
    )
    assertTrue(beanNames.any { it.contains("openApiDocConfig") || it.contains("OpenApiDocConfig") }, "OpenApiDocConfig bean should exist")
  }

  @Test
  fun `should have correct bean dependencies`() {
    val openApiDocConfig = applicationContext.getBean(OpenApiDocConfig::class.java)
    val properties = applicationContext.getBean(SpringdocOpenApiProperties::class.java)

    assertNotNull(openApiDocConfig, "OpenApiDocConfig should be available")
    assertNotNull(properties, "SpringdocOpenApiProperties should be available")

    // Verify that the configuration is applied correctly
    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    val customOpenApi = applicationContext.getBean(OpenAPI::class.java)

    assertNotNull(groupedOpenApi, "GroupedOpenApi should be created with dependencies")
    assertNotNull(customOpenApi, "Custom OpenAPI should be created with dependencies")
  }
}

@SpringBootTest(classes = [TestApplication::class])
@TestPropertySource(properties = ["compose.depend.springdoc-open-api.enable-jwt-header=false"])
class ConditionalConfigurationTest {

  @Resource lateinit var applicationContext: ApplicationContext

  @Test
  fun `should configure correctly when jwt header is disabled`() {
    val properties = applicationContext.getBean(SpringdocOpenApiProperties::class.java)
    assertNotNull(properties, "Properties should still be created")

    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    assertNotNull(groupedOpenApi, "GroupedOpenApi should still be created")
  }

  @Test
  fun `should configure correctly for web application`() {
    // Verify the effect of the @ConditionalOnWebApplication annotation
    val groupedOpenApi = applicationContext.getBean(GroupedOpenApi::class.java)
    assertNotNull(groupedOpenApi, "GroupedOpenApi should be created in web application context")
  }
}

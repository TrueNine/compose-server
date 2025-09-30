plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  SpringDoc OpenAPI 3 integration for automatic API documentation generation.
  Provides Swagger UI, OpenAPI specification generation, and comprehensive API documentation tools.
  """
    .trimIndent()

dependencies {
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
  api(projects.shared)

  implementation(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}

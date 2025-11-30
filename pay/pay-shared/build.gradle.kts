plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Shared payment processing abstractions and common models.
  Provides unified interfaces, enums, and data models for payment operations across different payment providers.
  """
    .trimIndent()

dependencies {
  api(projects.shared)

  implementation(libs.org.springframework.boot.spring.boot.starter.web)
  implementation(libs.io.swagger.core.v3.swagger.annotations.jakarta)
  implementation(libs.jakarta.servlet.jakarta.servlet.api)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}

plugins { 
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description = """
OAuth2 authentication and authorization implementation with support for various providers.
Provides OAuth2 client configurations, token management, and third-party integration capabilities.
""".trimIndent()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)

  implementation(projects.security.securityCrypto)
  implementation(libs.org.springframework.security.spring.security.core)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  testImplementation(projects.testtoolkit)
}

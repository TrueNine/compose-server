plugins {
  `kotlinspring-convention`
  `publish-convention`
}

version = libs.versions.composeCore.get()

dependencies {
  implementation(projects.meta)

  api(libs.com.fasterxml.jackson.core.jacksonAnnotations)
  api(libs.jakarta.annotation.jakartaAnnotationApi)
  api(libs.jakarta.servlet.jakartaServletApi)
  api(libs.io.swagger.core.v3.swaggerAnnotationsJakarta)
  api(libs.org.slf4j.slf4jApi)

  implementation(libs.org.springframework.security.springSecurityCrypto)

  testImplementation(projects.testToolkit)
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
}

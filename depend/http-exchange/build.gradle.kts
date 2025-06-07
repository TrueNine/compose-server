plugins {
  `kotlin-convention`
}

version = libs.versions.compose.depend.get()

dependencies {
  api(projects.shared)

  implementation(libs.org.springframework.spring.core)
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  implementation(libs.org.springframework.spring.web)
  implementation(libs.io.netty.netty.handler)
  implementation(libs.org.springframework.spring.webflux)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.webflux)
}

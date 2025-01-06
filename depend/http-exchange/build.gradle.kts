plugins {
  `kotlin-convention`
}

version = libs.versions.composeDependHttpExchange.get()

dependencies {
  implementation(projects.core)

  implementation(libs.org.springframework.springCore)
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(libs.org.springframework.springWeb)
  implementation(libs.io.netty.nettyHandler)
  implementation(libs.org.springframework.springWebflux)

  testImplementation(libs.org.springframework.boot.springBootStarterWebflux)
}

plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
  alias(libs.plugins.org.springframework.boot)
  alias(libs.plugins.com.google.devtools.ksp)
}

dependencies {
  implementation(libs.org.springframework.boot.spring.boot.starter.web)

  testImplementation(projects.testtoolkit)
}

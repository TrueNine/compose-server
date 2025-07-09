plugins {
  id("buildlogic.kotlinspring-conventions")
}

version = libs.versions.compose.oss.get()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  api(projects.shared)
  testImplementation(projects.testtoolkit)
}

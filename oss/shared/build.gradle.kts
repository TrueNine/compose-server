plugins {
  id("buildlogic.kotlinspring-conventions")
}


dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  api(projects.shared)
  testImplementation(projects.testtoolkit)
}

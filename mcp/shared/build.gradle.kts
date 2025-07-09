plugins {
  id("buildlogic.kotlinspring-conventions")
}

version = libs.versions.compose.ai.get()

dependencies {
  implementation(projects.shared)
  testImplementation(projects.testtoolkit)
}

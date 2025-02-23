plugins {
  `kotlin-convention`
}

version = libs.versions.compose.meta.get()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.annotations)

  testImplementation(projects.core)
  testImplementation(projects.testtoolkit)
}

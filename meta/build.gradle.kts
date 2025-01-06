plugins {
  `kotlin-convention`
}

version = libs.versions.composeMeta.get()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jacksonAnnotations)

  testImplementation(projects.core)
  testImplementation(projects.testToolkit)
}

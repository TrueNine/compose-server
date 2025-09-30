plugins {
  id("buildlogic.kotlin-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Metadata and annotation processing utilities providing compile-time code generation capabilities.
  Contains custom annotations and processors for enhancing development productivity and code quality.
  """
    .trimIndent()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.annotations)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}

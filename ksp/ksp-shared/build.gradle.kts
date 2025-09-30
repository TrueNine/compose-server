plugins {
  id("buildlogic.kotlin-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Shared utilities and base classes for Kotlin Symbol Processing (KSP) development.
  Provides common KSP APIs, KotlinPoet utilities, and shared metadata for code generation tasks.
  """
    .trimIndent()

dependencies {
  api(libs.com.google.devtools.ksp.symbol.processing.api)
  api(libs.com.squareup.kotlinpoet.jvm)
  api(libs.com.squareup.kotlinpoet.ksp)
  api(projects.ksp.kspMeta)
}

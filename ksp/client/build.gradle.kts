plugins { `kotlin-convention` }

version = libs.versions.compose.ksp.client.get()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbol.processing.api)

  implementation(libs.com.squareup.kotlinpoet.jvm)
  implementation(libs.com.squareup.kotlinpoet.ksp)

  implementation(projects.core)
  implementation(projects.meta)
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  implementation(libs.com.fasterxml.jackson.module.jackson.module.kotlin)

  implementation(projects.ksp.kspToolkit)
}

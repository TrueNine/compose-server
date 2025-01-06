plugins {
  `kotlin-convention`
}

version = libs.versions.composeKspClient.get()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbolProcessingApi)

  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)

  implementation(projects.core)
  implementation(projects.meta)
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)

  implementation(projects.ksp.kspToolkit)
}

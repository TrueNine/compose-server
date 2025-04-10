plugins { `kotlin-convention` }

version = libs.versions.compose.build.get()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbol.processing.api)

  implementation(libs.com.squareup.kotlinpoet.jvm)
  implementation(libs.com.squareup.kotlinpoet.ksp)

  api(projects.shared)
  implementation(projects.meta)
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  implementation(libs.com.fasterxml.jackson.module.jackson.module.kotlin)

  implementation(projects.ksp.kspShared)
}

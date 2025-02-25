plugins { `kotlin-convention` }

version = libs.versions.compose.ksp.toolkit.get()

dependencies {
  api(libs.com.google.devtools.ksp.symbol.processing.api)
  api(libs.com.squareup.kotlinpoet.jvm)
  api(libs.com.squareup.kotlinpoet.ksp)

  api(projects.meta)
}

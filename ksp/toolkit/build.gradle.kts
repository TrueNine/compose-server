plugins {
  `kotlin-convention`
}

version = libs.versions.composeKspToolkit.get()

dependencies {
  api(libs.com.google.devtools.ksp.symbolProcessingApi)
  api(libs.com.squareup.kotlinpoetJvm)
  api(libs.com.squareup.kotlinpoetKsp)
  
  api(projects.meta)
}

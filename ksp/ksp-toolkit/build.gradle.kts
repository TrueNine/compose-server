project.version = libs.versions.compose.ksp.asProvider().get()

dependencies {
  implementation(libs.com.google.devtools.ksp.symbolProcessingApi)
  implementation(libs.com.squareup.javapoet)
  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)
  implementation(libs.org.jetbrains.kotlinx.kotlinxIoCore)
  implementation(libs.org.jetbrains.kotlinx.kotlinxIoCoreJvm)
  implementation(project(":ksp:ksp-core"))
  implementation(project(":core"))
}
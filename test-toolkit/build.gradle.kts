version = libs.versions.compose.get()

dependencies {
  api(libs.bundles.knife4j)
  implementation(project(":core"))

  api(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)
  api(libs.jakarta.openapiV3Annotations)
}

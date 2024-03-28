version = libs.versions.compose.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.spring.doc.webmvcUi3)
  // api(libs.bundles.knife4j)
  // api(libs.jakarta.openapiV3Annotations)
}

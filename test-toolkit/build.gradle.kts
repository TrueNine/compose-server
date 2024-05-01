version = libs.versions.compose.get()

dependencies {
  api(libs.bundles.knife4j)
  implementation(project(":core"))

  api(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
}

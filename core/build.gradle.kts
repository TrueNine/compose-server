version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.jackson.coreAnnotations)

  api(libs.util.ognl)
  api(libs.util.guavaJre)

  api(libs.jakarta.servletApi)
  api(libs.jakarta.validationApi)

  api(libs.slf4j.api)
  api(libs.jakarta.openapiV3Annotations)
  api(libs.spring.modulith.core)

  implementation(libs.spring.boot.json)
  implementation(libs.spring.security.crypto)
  implementation(libs.security.bcprovJdk18on)
  implementation(libs.spring.webmvc)

  // TODO 日志
  implementation(libs.spring.boot.logging)

  // TODO hutool
  implementation(libs.util.hutool.core)
  implementation(libs.util.hutool.crypto)

  testImplementation(libs.spring.boot.web)
  testImplementation(libs.spring.boot.json)
}

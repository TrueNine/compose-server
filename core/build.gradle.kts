import net.yan100.compose.plugin.annotationProcessorKapt

version = libs.versions.compose.get()

dependencies {
  api(libs.json.jacksonCoreAnnotations)
  api(libs.jakarta.validationApi)
  api(libs.org.slf4j.slf4j.api)
  api(libs.jakarta.openapiV3Annotations)
  api(libs.jakarta.annotationApi)

  implementation(libs.jakarta.servletApi)
  implementation(libs.spring.modulith.core)

  implementation(libs.org.mapstruct.mapstruct.asProvider())
  annotationProcessorKapt(libs.org.mapstruct.mapstruct.processor)

  implementation(libs.org.springframework.boot.spring.boot.starter.json)
  implementation(libs.spring.security.crypto)
  implementation(libs.security.bcprovJdk18on)

  // TODO 日志
  implementation(libs.spring.boot.logging)

  // TODO hutool
  implementation(libs.cn.hutool.hutool.core)
  implementation(libs.cn.hutool.hutool.crypto)

  testImplementation(libs.spring.boot.web)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)
}

import net.yan100.compose.plugin.annotationProcessorKapt

version = libs.versions.compose.core.get()

dependencies {
  api(libs.json.jacksonCoreAnnotations)
  api(libs.jakarta.validationApi)
  api(libs.org.slf4j.slf4j.api)
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
  api(libs.jakarta.annotation.jakarta.annotation.api)

  implementation(libs.jakarta.servletApi)
  implementation(libs.spring.modulith.core)

  implementation(libs.org.mapstruct.mapstruct.asProvider())
  annotationProcessorKapt(libs.org.mapstruct.mapstruct.processor)

  implementation(libs.org.springframework.boot.spring.boot.starter.json)
  implementation(libs.spring.security.crypto)
  implementation(libs.org.bouncycastle.bcprov.jdk18on)

  // TODO 日志
  implementation(libs.spring.boot.logging)

  // TODO hutool
  implementation(libs.cn.hutool.hutool.core)
  implementation(libs.cn.hutool.hutool.crypto)

  testImplementation(libs.spring.boot.web)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}

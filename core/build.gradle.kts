version = libs.versions.compose.core.get()

dependencies {
  api(libs.com.fasterxml.jackson.core.jacksonAnnotations)
  api(libs.jakarta.validation.jakartaValidationApi)
  api(libs.jakarta.persistence.jakartaPersistenceApi)
  api(libs.jakarta.annotation.jakartaAnnotationApi)
  api(libs.jakarta.servlet.jakartaServletApi)
  api(libs.io.swagger.core.v3.swaggerAnnotationsJakarta)
  api(libs.org.slf4j.slf4jApi)

  implementation(libs.org.springframework.modulith.springModulithCore)
  implementation(libs.org.mapstruct.mapstruct)

  kapt(libs.org.mapstruct.mapstructProcessor)
  annotationProcessor(libs.org.mapstruct.mapstructProcessor)

  implementation(libs.org.springframework.boot.springBootStarterJson)
  implementation(libs.org.springframework.security.springSecurityCrypto)
  implementation(libs.org.bouncycastle.bcprovJdk18on)

  // TODO 日志
  implementation(libs.org.springframework.boot.springBootStarterLogging)

  // TODO hutool
  implementation(libs.cn.hutool.hutoolCore)
  implementation(libs.cn.hutool.hutoolCrypto)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  testImplementation(libs.org.springframework.boot.springBootStarterJson)
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

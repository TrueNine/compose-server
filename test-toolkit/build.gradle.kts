version = libs.versions.compose.testToolkit.get()

dependencies {
  api(libs.org.slf4j.slf4jApi)
  api(libs.org.springframework.boot.springBootStarterWeb)

  // json
  api(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)
  api(libs.com.fasterxml.jackson.core.jacksonDatabind)
  api(libs.org.skyscreamer.jsonassert)

  // spring 测试支持
  api(libs.org.springframework.springTest)
  api(libs.org.springframework.boot.springBootTest)
  api(libs.org.springframework.boot.springBootTestAutoconfigure)
  api(libs.org.springframework.security.springSecurityTest)

  // spring web
  api(libs.org.springframework.boot.springBootStarterWeb)
  api(libs.org.springframework.boot.springBootStarterTomcat)
  api(libs.org.springframework.boot.springBootStarterJson)

  // 日志自动配置
  runtimeOnly(libs.org.springframework.boot.springBootStarterLogging)

  // jsr 303
  api(libs.org.springframework.boot.springBootStarterValidation)
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

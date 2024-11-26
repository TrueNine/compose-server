version = libs.versions.composeTestToolkit.get()

dependencies {
  api(libs.org.jetbrains.kotlin.kotlinTestJunit5)
  api(libs.io.mockk.mockk)

  implementation(libs.org.junit.jupiter.junitJupiterApi)
  implementation(libs.org.slf4j.slf4jApi)

  // json
  api(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)
  api(libs.com.fasterxml.jackson.core.jacksonDatabind)
  runtimeOnly(libs.org.skyscreamer.jsonassert)

  // spring 测试支持
  runtimeOnly(libs.org.springframework.springTest)
  runtimeOnly(libs.org.springframework.boot.springBootTest) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
  }
  runtimeOnly(libs.org.springframework.boot.springBootTestAutoconfigure)
  implementation(libs.org.springframework.security.springSecurityTest)
  implementation(libs.org.springframework.boot.springBootTestAutoconfigure)

  // spring web
  api(libs.org.springframework.boot.springBootStarterWeb)
  api(libs.org.springframework.boot.springBootStarterTest) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
  }
  runtimeOnly(libs.org.springframework.boot.springBootStarterJson)
  runtimeOnly(libs.org.springframework.boot.springBootStarterTomcat)

  // spring batch
  api(libs.org.springframework.batch.springBatchTest)

  // 日志自动配置
  runtimeOnly(libs.org.springframework.boot.springBootStarterLogging)

  // jsr 303
  implementation(libs.org.springframework.boot.springBootStarterValidation)

  // 测试用数据库
  runtimeOnly(libs.org.hsqldb.hsqldb)
  runtimeOnly(libs.com.h2database.h2)
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

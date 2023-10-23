version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.jackson.core.annotations)

  api(libs.util.ognl)
  api(libs.util.guava.jre)

  api(libs.jakarta.servlet.api)
  api(libs.jakarta.validation.api)

  api(libs.slf4j.api)
  api(libs.jakarta.openapi.v3.annotations)
  api(libs.spring.modulith.core)

  implementation(libs.spring.boot.json)
  implementation(libs.spring.security.crypto)
  implementation(libs.security.bcprov.jdk18on)
  implementation(libs.spring.webmvc)

  // TODO 日志
  implementation(libs.spring.boot.logging)

  // TODO hutool
  implementation(libs.util.hutool.core)
  implementation(libs.security.hutool.crypto)

  testImplementation(libs.spring.boot.web)
  testImplementation(libs.spring.boot.json)
}

tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}

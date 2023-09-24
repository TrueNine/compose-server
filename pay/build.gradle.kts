version = libs.versions.compose.pay.get()

dependencies {
  api(libs.sdk.pay.wechatv3)
  implementation(libs.spring.boot.validation)
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
  implementation(project(":security:security-oauth2"))
}

tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}

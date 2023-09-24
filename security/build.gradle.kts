project.version = libs.versions.compose.security.asProvider().get()

dependencies {
  api(libs.spring.boot.security)
  api(libs.security.hutool.captcha)
  api(libs.security.jwt.auth0)
  implementation(libs.spring.webmvc)
  implementation(libs.security.antisamy) {
    //exclude(group = "org.htmlunit:neko-htmlunit", module = "neko-htmlunit")
    //implementation(libs.cralwer.nekohtml)
  }
  implementation(project(":core"))
}


tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}


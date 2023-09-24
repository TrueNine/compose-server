project.version = libs.versions.compose.security.asProvider().get()

dependencies {
  api(libs.spring.boot.security)
  api(libs.security.hutool.captcha)
  api(libs.security.jwt.auth0)
  implementation(libs.spring.webmvc)
  implementation(libs.security.antisamy) {
    exclude(group = "net.sourceforge.nekohtml", module = "nekohtml")
    implementation(libs.cralwer.nekohtml)
  }
  implementation(project(":core"))
}



tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}


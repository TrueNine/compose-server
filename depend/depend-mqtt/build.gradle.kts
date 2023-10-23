version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.spring.integration.mqtt)
  implementation(project(":core"))
}

tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}

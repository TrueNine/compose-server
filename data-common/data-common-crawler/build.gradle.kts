version = libs.versions.compose.get()

dependencies {
  api(project(":data-common:data-common-data-extract"))
  implementation(project(":core"))
  api(libs.bundles.selenium)
  api(libs.cralwer.playwright)
  implementation(libs.util.ikanalyzer)
  implementation(libs.util.smilemath)
}

tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}

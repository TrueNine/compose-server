version = libs.versions.compose.datacommon.dataextract.get()

dependencies {
  api(libs.net.okhttp3)
  api(libs.cralwer.jsoup)
  api(libs.db.supercsv)
  api(libs.util.easyexcel) {
    exclude("org.apache.commons", "commons-compress")
    implementation(libs.apache.commons.compress)
  }
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
}

tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}

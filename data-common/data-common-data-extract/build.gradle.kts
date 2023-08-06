import net.yan100.compose.plugin.V

project.version = V.Compose.dataCommonDataExtract

dependencies {
  api("com.squareup.okhttp3:okhttp:${V.Web.okhttp3}")
  api("org.jsoup:jsoup:${V.Util.jsoup}")
  api("com.alibaba:easyexcel:${V.Util.easyExcel}") {
    exclude("org.apache.commons", "commons-compress")
    api("org.apache.commons:commons-compress:${V.Util.commonsCompress}")
  }
  api("net.sf.supercsv:super-csv:${V.Util.superCsv}")
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}

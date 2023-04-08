project.version = V.Component.dataCommonDataExtract

dependencies {
  api("org.jsoup:jsoup:${V.Util.jsoup}")
  api("com.alibaba:easyexcel:${V.Util.easyExcel}") {
    exclude("org.apache.commons", "commons-compress")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    implementation("org.apache.commons:commons-compress:${V.Util.commonsCompress}")
  }
  api("net.sf.supercsv:super-csv:${V.Util.superCsv}")
  implementation(project(":core"))
}

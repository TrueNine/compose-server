project.version = V.Project.dataCommonDataExtract

dependencies {
  api("org.jsoup:jsoup:${V.Util.jsoup}")
  api("com.alibaba:easyexcel:${V.Office.easyExcel}") {
    exclude("org.apache.commons", "commons-compress")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    implementation("org.apache.commons:commons-compress:${V.Util.commonsCompress}")
  }
  api("net.sf.supercsv:super-csv:${V.Office.superCsv}")
  implementation(project(":core"))
}

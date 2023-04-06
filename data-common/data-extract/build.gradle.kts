project.version = V.Project.dataExtract

dependencies {
  api("org.jsoup:jsoup:${V.Util.jsoup}")
  api("com.alibaba:easyexcel:${V.Office.easyExcel}")
  api("org.apache.commons:commons-csv:${V.Office.commonsCsv}")
  implementation(project(":core"))
}

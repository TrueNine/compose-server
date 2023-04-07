project.version = V.Project.dataCommonDataExtract

dependencies {
  api("org.jsoup:jsoup:${V.Util.jsoup}")
  api("com.alibaba:easyexcel:${V.Office.easyExcel}")
  // https://mvnrepository.com/artifact/net.sf.supercsv/super-csv
  api("net.sf.supercsv:super-csv:${V.Office.superCsv}")
  implementation(project(":core"))
}

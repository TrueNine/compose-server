project.version = V.Component.dataCommonDataExtract

dependencies {
  api("org.jsoup:jsoup")
  api("com.alibaba:easyexcel") {
    exclude("org.apache.commons", "commons-compress")
    implementation("org.apache.commons:commons-compress")
  }
  api("net.sf.supercsv:super-csv")
  implementation(project(":core"))
}

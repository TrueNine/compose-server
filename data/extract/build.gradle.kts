plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeDataExtract.get()

dependencies {
  implementation(libs.bundles.kotlinReactor)
  implementation(libs.org.springframework.springWeb)
  implementation(libs.org.springframework.boot.springBootStarterWebflux)
  api(libs.org.jsoup.jsoup)
  api(libs.net.sf.supercsv.superCsv)

  api(libs.com.alibaba.easyexcel) {
    exclude("org.apache.commons", "commons-compress")
    implementation(libs.org.apache.commons.commonsCompress)
  }

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)

  testImplementation(projects.testToolkit)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbinding)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbindingAllPlatforms)
}

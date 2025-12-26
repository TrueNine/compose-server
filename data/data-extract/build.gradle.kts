plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Data extraction utilities supporting multiple formats including Excel, CSV, and web scraping.
  Provides EasyExcel integration, JSoup for HTML parsing, and reactive data processing capabilities.
  """
    .trimIndent()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.reactor)
  implementation(libs.io.projectreactor.kotlin.reactor.kotlin.extensions)
  implementation(libs.org.springframework.spring.web)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  api(libs.org.jsoup.jsoup)
  api(libs.net.sf.supercsv.`super`.csv)

  // Deprecated
  api(libs.com.alibaba.easyexcel) {
    exclude(group = libs.org.apache.commons.commons.compress.get().module.group, module = libs.org.apache.commons.commons.compress.get().module.name)
  }
  // New
  implementation(libs.cn.idev.excel.fastexcel)

  implementation(libs.org.apache.commons.commons.compress)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)

  testImplementation(projects.testtoolkit.testtoolkitShared)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbinding)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbinding.all.platforms)
}

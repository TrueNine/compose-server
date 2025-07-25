plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Data extraction utilities supporting multiple formats including Excel, CSV, and web scraping.
Provides EasyExcel integration, JSoup for HTML parsing, and reactive data processing capabilities.
"""
    .trimIndent()

dependencies {
  implementation(libs.bundles.kotlin.reactor)
  implementation(libs.org.springframework.spring.web)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  api(libs.org.jsoup.jsoup)
  api(libs.net.sf.supercsv.`super`.csv)

  api(libs.com.alibaba.easyexcel) {
    exclude(group = libs.org.apache.commons.commons.compress.get().module.group, module = libs.org.apache.commons.commons.compress.get().module.name)
  }
  implementation(libs.org.apache.commons.commons.compress)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbinding)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbinding.all.platforms)
}

plugins {
  `kotlin-convention`
}

version = libs.versions.composeDataCrawler.get()

dependencies {
  implementation(projects.core)

  implementation(libs.bundles.selenium)
  api(libs.com.microsoft.playwright.playwright)

  implementation(libs.com.github.magese.ikAnalyzer)
  implementation(libs.com.github.haifengl.smileMath)

  testImplementation(projects.testToolkit)
}

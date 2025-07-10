plugins {
  id("buildlogic.kotlin-conventions")
}

description = """
Web crawling and data scraping utilities with support for multiple automation frameworks.
Includes Selenium WebDriver, Microsoft Playwright, and intelligent text analysis capabilities.
""".trimIndent()

dependencies {
  api(projects.shared)

  implementation(libs.bundles.selenium)
  api(libs.com.microsoft.playwright.playwright)

  implementation(libs.com.github.magese.ik.analyzer)
  implementation(libs.com.github.haifengl.smile.math)

  testImplementation(projects.testtoolkit)
}

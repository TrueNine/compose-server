plugins {
  id("buildlogic.kotlin-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Web crawling and data scraping utilities with support for multiple automation frameworks.
Includes Selenium WebDriver, Microsoft Playwright, and intelligent text analysis capabilities.
"""
    .trimIndent()

dependencies {
  api(projects.shared)

  api(libs.com.microsoft.playwright.playwright)

  testImplementation(projects.testtoolkit)
}

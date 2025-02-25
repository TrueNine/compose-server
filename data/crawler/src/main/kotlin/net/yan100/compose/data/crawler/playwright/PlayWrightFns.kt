package net.yan100.compose.data.crawler.playwright

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright

fun Playwright.launchBy(
  browserType: BrowserTypes = BrowserTypes.CHROMIUM,
  headless: Boolean? = false,
  launchTimeout: Double = 30000.0,
): Browser {
  return browserType
    .getPlaywrightType(this)
    .launch(
      BrowserType.LaunchOptions()
        .setHeadless(headless ?: false)
        .setTimeout(launchTimeout)
    )
}

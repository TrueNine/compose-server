package io.github.truenine.composeserver.data.crawler.playwright

import com.microsoft.playwright.*

fun Playwright.launchBy(browserType: BrowserTypes = BrowserTypes.CHROMIUM, headless: Boolean? = false, launchTimeout: Double = 30000.0): Browser {
  return browserType.getPlaywrightType(this).launch(BrowserType.LaunchOptions().setHeadless(headless ?: false).setTimeout(launchTimeout))
}

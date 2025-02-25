package net.yan100.compose.data.crawler.playwright

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright

enum class BrowserTypes {
  CHROMIUM,
  WEBKIT,
  FIREFOX;

  fun getPlaywrightType(playwright: Playwright): BrowserType {
    return when (this) {
      CHROMIUM -> playwright.chromium()
      WEBKIT -> playwright.webkit()
      FIREFOX -> playwright.firefox()
    }
  }
}

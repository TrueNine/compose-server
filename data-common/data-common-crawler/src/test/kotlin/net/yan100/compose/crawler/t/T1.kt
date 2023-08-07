package net.yan100.compose.crawler.t

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import net.yan100.compose.crawler.ext.localStorage
import net.yan100.compose.crawler.ext.sessionStorage
import org.testng.annotations.Test
import java.nio.file.Paths

class T1 {
  @Test
  fun testOpen() {
    Playwright.create().use { playwright ->
      playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(false)).use { browser ->
        val ctx = browser.newContext()
        browser.newPage().use { page ->
          ctx.cookies()
          page.navigate("https://www.baidu.com")
          val a = page.evaluate("navigator.userAgent")
          val local = page.localStorage
          val session = page.sessionStorage

          val elements = page.screenshot(Page.ScreenshotOptions())
          page.locator("//form[@id='form']").screenshot(Locator.ScreenshotOptions().setPath(Paths.get("example1.png")))
          page.screenshot(Page.ScreenshotOptions().setPath(Paths.get("example.png")))
        }
      }
    }
  }

}

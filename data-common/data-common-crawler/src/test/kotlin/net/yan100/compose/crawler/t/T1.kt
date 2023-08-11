package net.yan100.compose.crawler.t

import com.microsoft.playwright.*
import com.microsoft.playwright.options.*
import net.yan100.compose.crawler.playwright.*
import org.testng.annotations.Test
import java.nio.file.Paths

class T1 {
  @Test
  fun testOpen() {
    Playwright.create().use { playwright ->
      playwright.launchBy(headless = false).use { browser ->
        browser.withActions(
          Browser.NewContextOptions().setColorScheme(ColorScheme.LIGHT).setViewportSize(1920, 1080),
        ) { _, page ->
          page.navigate("https://www.bilibili.com/")
          page.querySelector("#nav-searchform div.nav-search-content input.nav-search-input").fill("我的滑板鞋")
          page.waitForPopup {
            page.locator("#nav-searchform").getByRole(AriaRole.IMG).nth(1).click()
          }.use { searchPage ->
            searchPage.locatorWaitFor("//span[@class='vui_tabs--nav-text' and text() = '视频']").click()
            searchPage.waitForPageAllLoaded()
            var next: Boolean
            var step = 0
            do {
              searchPage.waitForPageAllLoaded()
              val text = searchPage.querySelector("//div[contains(@class,'video-list')]").querySelectorAll("//div[contains(@class,'video-list-item')]")
                .map { it.innerText() }
              println(text)
              val btn = searchPage.locatorWaitFor("//button[contains(@class ,'vui_pagenation--btn') and contains(text(),'下一页')]")
              val disabled = btn.getAttribute("disabled")
              next = disabled == null

              searchPage.screenshot(
                Page.ScreenshotOptions().setFullPage(true).setType(ScreenshotType.PNG).setPath(Paths.get("example${step}.png"))
              )
              step += 1
              if (next) btn.click()
            } while (next)
          }
        }
      }
    }
  }

  @Test
  fun t2() {
    Playwright.create()
    CLI.main(arrayOf("codegen", "bilibili.com"))
  }
}

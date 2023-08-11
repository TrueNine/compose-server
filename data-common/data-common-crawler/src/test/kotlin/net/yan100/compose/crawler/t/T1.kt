package net.yan100.compose.crawler.t

import com.microsoft.playwright.*
import com.microsoft.playwright.options.*
import net.yan100.compose.crawler.playwright.*
import org.testng.annotations.Test
import java.nio.file.Paths
import java.time.LocalDateTime
import kotlin.random.Random

class T1 {
  @Test
  fun testOpen() {
    Playwright.create().use { playwright ->
      playwright.launchBy(headless = false).use { browser ->
        browser.withActions(
          Browser.NewContextOptions().setColorScheme(ColorScheme.LIGHT).setViewportSize(1920, 1080),
        ) { _, page ->
          val locals = page.loopNext<Any>()
            .init {
              it.navigate("https://www.bilibili.com/")
              it.querySelector("#nav-searchform div.nav-search-content input.nav-search-input").fill("我的滑板鞋")
              val newPage = it.waitForPopup {
                it.locatorWaitFor("#nav-searchform").getByRole(AriaRole.IMG).nth(1).click()
              }
              newPage.locatorWaitFor("//span[contains(@class,'vui_tabs--nav-text') and text()='视频']").click()
              newPage
            }.execute {
              val b = it.screenshot(
                Page.ScreenshotOptions().setFullPage(true).setType(ScreenshotType.PNG).setPath(Paths.get("example-${LocalDateTime.now().nano}.png"))
              )
              it.localStorage()
            }.endWith {
              val btn = it.locatorWaitFor("//button[contains(@class ,'vui_pagenation--btn') and contains(text(),'下一页')]")
              val disabled = btn.getAttribute("disabled")
              val next = disabled == null
              if (next) {
                btn.click()
                it.waitForPageAllLoaded()
              }
              !next
            }
            .run()
          println(locals)
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

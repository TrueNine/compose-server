/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.crawler.t

import com.microsoft.playwright.Browser
import com.microsoft.playwright.CLI
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.AriaRole
import com.microsoft.playwright.options.ColorScheme
import com.microsoft.playwright.options.ScreenshotType
import java.nio.file.Paths
import java.time.LocalDateTime
import net.yan100.compose.crawler.playwright.*

class T1 {

  // @Test
  fun testOpen() {
    Playwright.create().use { playwright ->
      playwright.launchBy(headless = false).use { browser ->
        browser.withActions(
          Browser.NewContextOptions().setColorScheme(ColorScheme.LIGHT).setViewportSize(1920, 1080),
        ) { _, page ->
          val locals =
            page
              .loopNext<Any>()
              .init {
                it.navigate("https://www.bilibili.com/")
                it
                  .querySelector("#nav-searchform div.nav-search-content input.nav-search-input")
                  .fill("我的滑板鞋")
                val newPage =
                  it.waitForPopup {
                    it.locatorWaitFor("#nav-searchform").getByRole(AriaRole.IMG).nth(1).click()
                  }
                newPage
                  .locatorWaitFor("//span[contains(@class,'vui_tabs--nav-text') and text()='视频']")
                  .click()
                newPage
              }
              .execute {
                it.screenshot(
                  Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setType(ScreenshotType.PNG)
                    .setPath(Paths.get("example-${LocalDateTime.now().nano}.png"))
                )
                it.localStorage()
              }
              .endWith {
                val btn =
                  it.locatorWaitFor(
                    "//button[contains(@class ,'vui_pagenation--btn') and contains(text(),'下一页')]"
                  )
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

  fun t2() {
    Playwright.create()
    CLI.main(arrayOf("codegen", "bilibili.com"))
  }
}

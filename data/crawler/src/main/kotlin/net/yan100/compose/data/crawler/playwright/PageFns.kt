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
package net.yan100.compose.data.crawler.playwright

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Locator.WaitForOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.Cookie
import com.microsoft.playwright.options.LoadState
import com.microsoft.playwright.options.WaitForSelectorState

@Suppress("UNCHECKED_CAST")
fun Page.localStorage(): Map<String, String> {
  val a = this.evaluate("window.localStorage")
  return if (a == null || a !is Map<*, *>) mapOf() else a as Map<String, String>
}

fun Page.navigatorWebdriver(): Boolean {
  val d = this.evaluate("navigator.webdriver") as? Boolean?
  return d != null && d == true
}

fun Page.currentUserAgent(): String? = this.evaluate("navigator.userAgent") as? String?

@Suppress("UNCHECKED_CAST")
fun Page.sessionStorage(): Map<String, String> {
  val a = this.evaluate("window.sessionStorage")
  return if (a == null || a !is Map<*, *>) mapOf() else a as Map<String, String>
}

fun Page.cookies(): List<Cookie> = this.context().cookies()

fun Page.initHiddenWebDriver() {
  this.addInitScript("Object.defineProperties(navigator,{webdriver:{get:undefined}})")
}

fun Page.hiddenWebDriver() {
  this.evaluate("Object.defineProperties(navigator,{webdriver:{get:undefined}})")
}

fun Page.waitForPageAllLoaded(): Page {
  this.waitForLoadState(LoadState.LOAD)
  this.waitForLoadState(LoadState.NETWORKIDLE)
  this.waitForLoadState(LoadState.DOMCONTENTLOADED)
  return this
}

fun Page.locatorWaitFor(
  selector: String,
  locatorOptions: Page.LocatorOptions? = null,
  waitForOptions: WaitForOptions? = WaitForOptions().setState(WaitForSelectorState.ATTACHED),
): Locator {
  val l = this.locator(selector, locatorOptions)
  l.waitFor(waitForOptions)
  return l
}

fun <T : Any> Page.loopNext(): PageWaitChain<T> {
  return PageWaitChain(this)
}

class PageWaitChain<T : Any>(private var page: Page) {
  private var initFn: (Page) -> Page? = { it }
  private var stepFn: (Page) -> Page? = { it }
  private var waitFn: (Page) -> Page? = { it }
  private var endFn: (Page) -> Boolean = { true }

  @Suppress("UNCHECKED_CAST")
  private var exeFn: (Page) -> T = { Unit as T }

  fun init(i: (Page) -> Page?): PageWaitChain<T> {
    this.initFn = i
    return this
  }

  fun execute(exe: (Page) -> T): PageWaitChain<T> {
    this.exeFn = exe
    return this
  }

  fun waitForStep(wait: (Page) -> Page?): PageWaitChain<T> {
    this.waitFn = wait
    return this
  }

  fun endWith(end: (Page) -> Boolean): PageWaitChain<T> {
    this.endFn = end
    return this
  }

  fun run(): List<T> {
    val result = mutableListOf<T>()
    this.page = initFn(this.page) ?: return result
    do {
      this.page = waitFn(page) ?: return result
      result += exeFn(page)
    } while (!endFn(page))
    return result
  }
}

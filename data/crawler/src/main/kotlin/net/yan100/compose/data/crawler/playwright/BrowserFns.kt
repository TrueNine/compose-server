package net.yan100.compose.data.crawler.playwright

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page

fun Browser.withActions(ctxOptions: Browser.NewContextOptions? = null, pageOptions: Browser.NewPageOptions? = null, action: (BrowserContext, Page) -> Unit) {
  newContext(ctxOptions).use { ctx -> newPage(pageOptions).use { page -> action(ctx, page) } }
}

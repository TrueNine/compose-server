package net.yan100.compose.crawler.playwright

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page

fun Browser.withActions(
    ctxOptions: Browser.NewContextOptions? = null, pageOptions: Browser.NewPageOptions? = null, action: (BrowserContext, Page) -> Unit
) {
    this.newContext(ctxOptions).use { ctx ->
        this.newPage(pageOptions).use { page ->
            action(ctx, page)
        }
    }
}

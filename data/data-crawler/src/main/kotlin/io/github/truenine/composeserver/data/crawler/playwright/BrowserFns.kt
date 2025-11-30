package io.github.truenine.composeserver.data.crawler.playwright

import com.microsoft.playwright.*

fun Browser.withActions(ctxOptions: Browser.NewContextOptions? = null, pageOptions: Browser.NewPageOptions? = null, action: (BrowserContext, Page) -> Unit) {
  newContext(ctxOptions).use { ctx -> newPage(pageOptions).use { page -> action(ctx, page) } }
}

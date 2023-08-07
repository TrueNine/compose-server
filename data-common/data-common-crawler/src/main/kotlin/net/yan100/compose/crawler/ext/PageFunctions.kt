package net.yan100.compose.crawler.ext

import com.microsoft.playwright.Page


@Suppress("UNCHECKED_CAST")
val Page.localStorage: Map<String, String>
  get() = run {
    val a = this.evaluate("window.localStorage")
    if (a == null || a !is Map<*, *>) mapOf()
    else a as Map<String, String>
  }

@Suppress("UNCHECKED_CAST")
val Page.sessionStorage: Map<String, String>
  get() = run {
    val a = this.evaluate("window.sessionStorage")
    if (a == null || a !is Map<*, *>) mapOf()
    else a as Map<String, String>
  }

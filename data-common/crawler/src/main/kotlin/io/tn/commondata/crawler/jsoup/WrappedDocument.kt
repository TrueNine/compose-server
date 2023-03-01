package io.tn.commondata.crawler.jsoup

import io.tn.commondata.crawler.selenium.WrappedDriver
import io.tn.core.lang.KtLogBridge
import io.tn.core.lang.Str
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger

class WrappedDocument
private constructor(
  private val DOCUMENT: Document
) {
  fun findCss(cssQuery: String): Element? {
    return DOCUMENT.select(cssQuery).first()
  }

  fun findsCss(cssQuery: String): Elements {
    return DOCUMENT.select(cssQuery)
  }

  fun findXpath(xpath: String): Element? {
    return DOCUMENT.selectXpath(xpath).first()
  }

  fun findsXpath(xpath: String): Elements {
    return DOCUMENT.selectXpath(xpath)
  }

  override fun toString(): String {
    return DOCUMENT.toString()
  }

  companion object {
    private val log: Logger = KtLogBridge.getLog(WrappedDocument::class.java)

    @JvmStatic
    fun wrapper(document: Document): WrappedDocument {
      log.debug("wrapper doc doc = {}", Str.omit(document.toString(), 100))
      return WrappedDocument(document)
    }

    @JvmStatic
    fun wrapper(document: String): WrappedDocument {
      return WrappedDocument(Jsoup.parse(document))
    }

    @JvmStatic
    fun parse(html: String): Document {
      return Jsoup.parse(html)
    }

    @JvmStatic
    fun wrapper(driver: WrappedDriver): WrappedDocument {
      return wrapper(driver.allHtml())
    }

    @JvmStatic
    fun parse(element: Element): Document {
      return Jsoup.parse(element.toString())
    }

    @JvmStatic
    fun parse(elements: Elements): Document {
      return Jsoup.parse(elements.toString())
    }

    @JvmStatic
    fun toDocument(element: Element): Document {
      return parse(element.toString())
    }

    @JvmStatic
    fun toDocument(elements: Elements): Document {
      return parse(elements)
    }
  }
}

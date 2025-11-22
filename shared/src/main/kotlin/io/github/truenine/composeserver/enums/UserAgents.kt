package io.github.truenine.composeserver.enums

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IStringEnum

/**
 * Collection of user agent enums. Use val() method for invocation
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class UserAgents(private val ua: String) : IStringEnum {
  /** chrome windows 103 */
  CHROME_WIN_103("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"),
  CHROME_WIN_115("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"),

  /** edge windows 115 */
  EDGE_WIN_120("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0"),
  EDGE_WIN_115("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.203"),
  EDGE_WIN_106("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52");

  @JsonValue override val value: String = ua

  companion object {
    @JvmStatic operator fun get(v: String?) = findVal(v)

    @JvmStatic fun findVal(v: String?) = entries.find { it.ua == v }
  }
}

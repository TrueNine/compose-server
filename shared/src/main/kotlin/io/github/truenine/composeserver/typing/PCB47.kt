package io.github.truenine.composeserver.typing

import io.github.truenine.composeserver.IStringTyping

/**
 * # 各语言 按照 PBC47 标准的序列化字符串
 *
 * @author TrueNine
 * @since 2024-03-20
 */
enum class PCB47(private val primaryLang: String, vararg secondaryLanguages: String) : IStringTyping {
  ZH("zh"),
  EN("en"),
  ZH_CN("zh-CN"),
  ZH_HK("zh-HK"),
  ZH_TW("zh-TW"),
  EN_US("en-US");

  override val value: String
    get() = primaryLang

  /** ## 以 下华夏分割的 line */
  val underLineValue: String
    get() = primaryLang.replace("-", "_")

  companion object {
    @JvmStatic operator fun get(v: String?): PCB47? = entries.find { it.primaryLang == v }
  }
}

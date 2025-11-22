package io.github.truenine.composeserver.enums

import io.github.truenine.composeserver.IStringEnum

/**
 * # Language codes serialized according to the BCP 47 standard.
 *
 * @author TrueNine
 * @since 2024-03-20
 */
enum class PCB47(private val primaryLang: String, vararg secondaryLanguages: String) : IStringEnum {
  ZH("zh"),
  EN("en"),
  ZH_CN("zh-CN"),
  ZH_HK("zh-HK"),
  ZH_TW("zh-TW"),
  EN_US("en-US");

  override val value: String
    get() = primaryLang

  /** ## Value with hyphen replaced by underscore */
  val underLineValue: String
    get() = primaryLang.replace("-", "_")

  companion object {
    @JvmStatic operator fun get(v: String?): PCB47? = entries.find { it.primaryLang == v }
  }
}

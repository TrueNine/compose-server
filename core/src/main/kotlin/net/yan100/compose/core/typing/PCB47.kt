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
package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonValue

/**
 * # 各语言 按照 PBC47 标准的序列化字符串
 *
 * @author TrueNine
 * @since 2024-03-20
 */
enum class PCB47(private val primaryLang: String, vararg secondaryLanguages: String) : StringTyping {
  ZH("zh"),
  EN("en"),
  ZH_CN("zh-CN"),
  ZH_HK("zh-HK"),
  ZH_TW("zh-TW"),
  EN_US("en-US");

  @get:JsonValue
  override val value: String
    get() = primaryLang

  /** ## 以 下华夏分割的 line */
  val underLineValue: String
    get() = primaryLang.replace("-", "_")

  companion object {
    @JvmStatic
    operator fun get(v: String?): PCB47? = entries.find { it.primaryLang == v }
  }
}

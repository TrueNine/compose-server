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
package net.yan100.compose.plugin.properties

import java.util.*

/**
 * # gradle.properties 生成配置
 *
 * @author TrueNine
 * @since 2024-03-08
 */
open class GradlePropertiesGeneratorConfig :
  MutableMap<String, String> by mutableMapOf(
    "org.gradle.daemon" to "true",
    "org.gradle.parallel" to "true",
    "org.gradle.caching" to "false",
    "org.gradle.jvmargs" to "-Xmx8192m -Xms4096m",
    "org.gradle.workers.max" to Runtime.getRuntime().availableProcessors().toString()
  ) {
  fun workers(value: Int) {
    check(value in 0..1024) {
      "The number of counties you set up is too large, be careful that the computer is stuck"
    }
    this["org.gradle.workers.max"] = value.toString()
  }

  /** ## 添加其他选项 */
  fun otherOption(optionName: String, value: String) = put(optionName, value)

  fun toPropertiesString(): String {
    return buildString {
      asProperties().forEach {
        append(it.key)
        append("=")
        append(it.value)
        appendLine()
      }
    }
  }

  /** ## 转换为 properties */
  fun asProperties(): Properties {
    val p = Properties()
    p.putAll(this)
    return p
  }

  /** ## 设置 jvm args */
  fun jvmArgs(vararg value: String) {
    this["org.gradle.jvmargs"] = value.joinToString(separator = " ") { it.trim() }
  }

  /** ## 是否开启 gradle cache */
  fun caching(value: Boolean) {
    this["org.gradle.caching"] = value.toString()
  }

  /** ## 开启 gradle parallel */
  fun parallel(value: Boolean) {
    this["org.gradle.parallel"] = value.toString()
  }

  /** ## 是否开启 gradle daemon */
  fun daemon(value: Boolean) {
    this["org.gradle.daemon"] = value.toString()
  }

  companion object {
    const val GRADLE_PROPERTIES_NAME = "gradle.properties"
  }
}

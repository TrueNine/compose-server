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
 * # 所有类型枚举的抽象接口
 * 实现此接口，以方便其他序列化程序来读取枚举 实现此接口后，需要手动添加一个 findVal 静态方法，提供给 jackson等框架自动调用
 *
 * 由于无法在接口规定静态方法，此算作规约吧。以下为一个枚举类内部的静态方法示例
 *
 * ```kotlin
 * enum class GenderTyping(private val value: Int) {
 *   // ... other enum constants
 *   ;
 *     @get:JsonValue
 *     override val value = this.v
 *     companion object {
 *       @JvmStatic
 *       fun findVal(v: Int?) = GenderTyping.values().find { it.value == v }
 *     }
 * }
 * ```
 *
 * @author TrueNine
 * @since 2023-05-28
 */
@JvmDefaultWithoutCompatibility
interface AnyTyping {
  /** ## 获取枚举对应的实际值 */
  @get:JsonValue val value: Any

  companion object {
    @JvmStatic fun findVal(v: Any?): AnyTyping? = null
  }
}

/** # 数值型枚举 */
@JvmDefaultWithoutCompatibility
interface IntTyping : AnyTyping {
  @get:JsonValue override val value: Int

  companion object {
    @JvmStatic fun findVal(v: Int?): IntTyping? = null
  }
}

/** # 字符型枚举 */
interface StringTyping : AnyTyping {
  @get:JsonValue override val value: String

  companion object {
    @JvmStatic fun findVal(v: Int?): IntTyping? = null
  }
}

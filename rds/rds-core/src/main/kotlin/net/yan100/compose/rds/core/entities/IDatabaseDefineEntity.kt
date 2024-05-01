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
package net.yan100.compose.rds.core.entities

import kotlin.reflect.KProperty

/** # 数据库字段定义函数 */
sealed interface IDatabaseDefineEntity {

  @Suppress("UNCHECKED_CAST")
  class LateInitNonNullBasicValue<T> {
    var v: Any? = null

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
      if (null == thisRef) {
        throw NullPointerException("thisRef is null")
      }
      v = value as Any
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
      return  v as? T ?: throw NullPointerException("value is not initialized")
    }
  }
}

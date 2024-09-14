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
package net.yan100.compose.core

import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * ## 递归获取一个类的所有属性
 *
 * @param endType 结束的类型
 * @return 当前类以及所有到结束标记为止的 fields
 */
fun KClass<*>.recursionFields(endType: KClass<*> = Any::class): Array<out Field> {
  val selfFields = mutableListOf<Field>()
  var superClass: Class<*>? = this.java
  val endsWith = endType.java
  while (superClass != null) {
    selfFields += superClass.declaredFields
    superClass = superClass.superclass
    if (superClass == endsWith) break
  }
  return selfFields.toTypedArray()
}

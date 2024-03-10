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
package net.yan100.compose.rds.gen.converter

import net.yan100.compose.rds.gen.models.ConvertTypeModel

abstract class AbstractTypeConverter {
  private val converterRule: MutableMap<String, ConvertTypeModel> = mutableMapOf()
  private val defaultKey = "DATABASE_DEFAULT_CONVERTER_TYPE_NAMESPACE"

  init {
    converterRule[defaultKey] = ConvertTypeModel("String")
  }

  protected fun putAll(converters: Map<String, ConvertTypeModel>) {
    val b = mutableMapOf<String, ConvertTypeModel>()
    converters.forEach { b[it.key.uppercase()] = it.value }
    this.converterRule.putAll(b)
  }

  fun getConverterTypeModel(dbType: String): ConvertTypeModel {
    val getC = converterRule.filter { dbType.uppercase().contains(it.key) }.map { it.value }
    return if (getC.isEmpty()) {
      converterRule[defaultKey]!!
    } else {
      getC[0]
    }
  }

  fun getConverters(): Map<String, ConvertTypeModel> {
    return this.converterRule
  }

  fun findImports(types: List<String>): List<String> {
    return types.map { getConverterTypeModel(it) }.mapNotNull { it.importPkg }
  }
}

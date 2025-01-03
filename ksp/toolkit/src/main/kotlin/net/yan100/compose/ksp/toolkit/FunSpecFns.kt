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
package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSFunctionDeclaration.toFunSpec(): FunSpec {
  return FunSpec.builder(simpleName.asString())
    .also { k ->
      returnType?.toTypeName()?.also { t -> k.returns(t) }
      parameters.forEach { p -> k.addParameter(ParameterSpec.builder(p.name!!.asString(), p.type.toTypeName()).build()) }
      if (null != findOverridee()) k.addModifiers(KModifier.OVERRIDE)
      else {
        if (isPublic()) k.addModifiers(KModifier.PUBLIC)
        if (isOpen()) k.addModifiers(KModifier.OPEN)
      }
    }
    .build()
}

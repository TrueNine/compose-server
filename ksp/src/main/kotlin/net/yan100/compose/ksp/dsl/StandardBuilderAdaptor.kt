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
package net.yan100.compose.ksp.dsl

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import kotlin.reflect.KClass

interface StandardBuilderAdaptor<T, R> {
  val builder: T

  fun build(): R

  val fileBuilder: FileSpec.Builder

  fun importBy(pkg: String, vararg names: String) = fileBuilder.addImport(pkg, *names)

  fun importBy(classDeclaration: KSClassDeclaration) = fileBuilder.addImport(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())

  fun importBy(clazz: Class<*>) = fileBuilder.addImport(clazz)

  fun importBy(kClazz: KClass<*>) = fileBuilder.addImport(kClazz)
}

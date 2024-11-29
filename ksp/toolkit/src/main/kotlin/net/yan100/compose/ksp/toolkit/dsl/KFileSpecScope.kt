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
package net.yan100.compose.ksp.toolkit.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import kotlin.reflect.KClass

class KFileSpecScope @JvmOverloads constructor(
  packageName: String = "",
  fileName: String = "",
  className: ClassName? = null,
  memberName: MemberName? = null
) :
  StandardBuilderAdaptor<FileSpec.Builder, FileSpec> {
  private val fb: FileSpec.Builder =
    if (null != className) FileSpec.builder(className) else if (null != memberName) FileSpec.builder(memberName) else FileSpec.builder(packageName, fileName)

  fun annotateBy(cls: KClass<*>) = builder.addAnnotation(cls)

  fun classBy(className: ClassName, KClassSpecScope: KClassSpecScope.() -> Unit) =
    builder.addType(KClassSpecScope(className = className, fileBuilder = fb).apply(KClassSpecScope).build())

  override val fileBuilder = fb
  override val builder: FileSpec.Builder = fileBuilder

  override fun build(): FileSpec = fileBuilder.build()
}



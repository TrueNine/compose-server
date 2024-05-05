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

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import java.time.LocalDate

class KDocDsl(override val fileBuilder: FileSpec.Builder) : StandardBuilderAdaptor<CodeBlock.Builder, CodeBlock> {
  private val cb: CodeBlock.Builder = CodeBlock.builder()

  private fun addTitleFormat(title: String, count: Int = 1, vararg args: Any) {
    for (i in 1..count) cb.add("#")
    cb.add(" $title", args)
  }

  fun desc(doc: String, vararg args: Any) {
    if (cb.isEmpty()) cb.add(doc, args) else cb.add("\n$doc", args)
  }

  fun h1(title: String, vararg args: Any) = addTitleFormat(title, 1, args)

  fun h2(title: String, vararg args: Any) = addTitleFormat(title, 2, args)

  fun h3(title: String, vararg args: Any) = addTitleFormat(title, 3, args)

  fun h4(title: String, vararg args: Any) = addTitleFormat(title, 4, args)

  fun h5(title: String, vararg args: Any) = addTitleFormat(title, 5, args)

  fun h6(title: String, vararg args: Any) = addTitleFormat(title, 6, args)

  fun param(name: String, desc: String = name) = cb.add("@param $name $desc\n")

  fun returnType(desc: String = "") = cb.add("@return $desc\n")

  fun see(desc: String = "") = cb.add("@see $desc\n")

  fun since(version: String = LocalDate.now().toString()) = cb.add("@since $version\n")

  fun throws(type: String, desc: String = "") = cb.add("@throws $type $desc\n")

  fun throws(type: Throwable, desc: String = "") = cb.add("@throws ${type::class.qualifiedName} $desc\n")

  override fun build(): CodeBlock = cb.build()

  override val builder: CodeBlock.Builder
    get() = cb
}

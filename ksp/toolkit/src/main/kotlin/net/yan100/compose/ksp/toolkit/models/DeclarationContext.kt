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
package net.yan100.compose.ksp.toolkit.models

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitor
import java.util.concurrent.CopyOnWriteArraySet

data class DeclarationContext<D : KSDeclaration>(
  val declaration: D,
  val environment: SymbolProcessorEnvironment,
  val resolver: Resolver,
  val codeGenerator: CodeGenerator = environment.codeGenerator,
  val log: KSPLogger = environment.logger,
  val file: KSFile = declaration.containingFile!!,
  var dependencies: Dependencies = Dependencies.ALL_FILES,
  private val notProcessReportList: MutableSet<KSAnnotated> = CopyOnWriteArraySet()
) {
  /**
   * 调用 declaration 的 accept 方法
   */
  fun <R : Any?> accept(visitor: KSVisitor<DeclarationContext<D>, R>): R {
    return declaration.accept(visitor = visitor, data = this)
  }

  fun clearReported() {
    notProcessReportList.clear()
  }

  fun report(annotated: KSAnnotated) {
    notProcessReportList += annotated
  }
}

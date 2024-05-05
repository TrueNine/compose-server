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
package net.yan100.compose.ksp.entrance

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import net.yan100.compose.ksp.data.ContextData
import net.yan100.compose.ksp.data.FileContext
import net.yan100.compose.ksp.visitor.allClassVisitor

class Processor(private val environment: SymbolProcessorEnvironment, private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
  SymbolProcessor {
  private var invoked = false

  override fun process(resolver: Resolver): List<KSAnnotated> {
    if (invoked) return emptyList()
    invoked = true

    val allFiles = resolver.getAllFiles()

    allFiles.forEach { f ->
      allClassVisitor.forEach {
        val ctx = ContextData(environment, resolver, codeGenerator, logger, f, FileContext(f))
        f.accept(it, ctx)
      }
    }

    return emptyList()
  }
}

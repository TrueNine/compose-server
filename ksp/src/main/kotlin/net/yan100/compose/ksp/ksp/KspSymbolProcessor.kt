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
package net.yan100.compose.ksp.ksp /*
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

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStreamWriter

class Abs(
  val codeGenerator: CodeGenerator,
  val logger: KSPLogger,
) : SymbolProcessor {
  private var invoked = false

  override fun process(resolver: Resolver): List<KSAnnotated> {
    val handles = resolver.getSymbolsWithAnnotation(Abstract::class.simpleName!!)

    handles.forEach { println(it) }

    if (invoked) return emptyList()
    invoked = true

    codeGenerator.createNewFile(Dependencies.ALL_FILES, "com.abc.d", "a", "kt").use { output ->
      OutputStreamWriter(output).use { writer ->
        writer.write("package com.abc.d\n")
        writer.write("class Ab {\n")
        val visitor = ClassVisitor()

        resolver.getAllFiles().forEach { it.accept(visitor, writer) }

        writer.write("}\n")
      }
    }
    return emptyList()
  }
}

class ClassVisitor : KSTopDownVisitor<OutputStreamWriter, Unit>() {
  override fun defaultHandler(node: KSNode, data: OutputStreamWriter) {}

  override fun visitClassDeclaration(
    classDeclaration: KSClassDeclaration,
    data: OutputStreamWriter,
  ) {
    super.visitClassDeclaration(classDeclaration, data)
    val symbolName = classDeclaration.simpleName.asString().lowercase()
    data.write("    val $symbolName = true\n")
  }
}

class KspSymbolProcessor : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    return Abs(environment.codeGenerator, environment.logger)
  }
}

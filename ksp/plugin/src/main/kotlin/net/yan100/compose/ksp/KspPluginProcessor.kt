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
package net.yan100.compose.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.visitor.JpaNameClassVisitor
import net.yan100.compose.ksp.visitor.RepositoryIPageExtensionsVisitor

class KspPluginProcessor(
  private val environment: SymbolProcessorEnvironment,
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger
) : SymbolProcessor {
  private fun <D : KSDeclaration> getCtxData(declaration: D, resolver: Resolver): DeclarationContext<D> {
    return DeclarationContext(declaration, environment, resolver, codeGenerator, logger)
  }

  @OptIn(KspExperimental::class)
  override fun process(resolver: Resolver): List<KSAnnotated> {
    resolver.getSymbolsWithAnnotation(MetaDef::class.qualifiedName!!)
      .filterIsInstance<KSClassDeclaration>()
      .filter {
        it.isAbstract()
      }
      .filter {
        it.qName.let { s ->
          s?.startsWith("Super")
        } == true
      }
      .map {
        getCtxData(it, resolver)
      }.forEach { ctx ->
        ctx.declaration.accept(JpaNameClassVisitor(), ctx)
      }

    resolver.getPackagesWithAnnotation("org.springframework.stereotype.Repository").filterIsInstance<KSClassDeclaration>().map {
      RepositoryIPageExtensionsVisitor()
    }

    return emptyList()
  }
}

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

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import net.yan100.compose.ksp.toolkit.models.DeclarationContext
import net.yan100.compose.ksp.toolkit.simpleNameAsString
import net.yan100.compose.ksp.visitor.JpaNameClassVisitor
import net.yan100.compose.ksp.visitor.RepositoryIPageExtensionsVisitor
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaSkipGeneration

class KspPluginProcessor(
  private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
  private fun <D : KSDeclaration> getCtxData(declaration: D, resolver: Resolver): DeclarationContext<D> {
    return DeclarationContext(declaration, environment, resolver)
  }

  @OptIn(KspExperimental::class)
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val lis = resolver.getSymbolsWithAnnotation(
      "jakarta.persistence.EntityListener"
    ).filterIsInstance<KSDeclaration>().firstOrNull()?.let {
      it.annotations.firstOrNull()?.toAnnotationSpec()
    }
    val jpaSymbols = resolver.getSymbolsWithAnnotation("net.yan100.compose.meta.annotations.MetaDef")
    val nextSymbols = jpaSymbols.filter { !it.validate() }.toList()
    jpaSymbols
      .filter { it.validate() }
      .filterIsInstance<KSClassDeclaration>()
      .filterNot { nextSymbols.contains(it) }
      .filter { !it.isAnnotationPresent(MetaSkipGeneration::class) }
      .filter { it.getDeclaredProperties().toList().isNotEmpty() }
      .filter {
        !it.isCompanionObject
      }
      .filter { it.simpleNameAsString.startsWith("Super") }
      .filterNot { it.simpleNameAsString.contains("$") }
      .filter { it.getAnnotationsByType(MetaDef::class).toList().isNotEmpty() }
      .forEach {
        getCtxData(it, resolver).accept(
          JpaNameClassVisitor(lis)
        )
      }

    resolver.getPackagesWithAnnotation("org.springframework.stereotype.Repository")
      .filterIsInstance<KSClassDeclaration>()
      .filter { it.classKind == ClassKind.INTERFACE }
      .forEach { getCtxData(it, resolver).accept(RepositoryIPageExtensionsVisitor()) }
    return nextSymbols
  }
}

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
package net.yan100.compose.ksp.visitor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import net.yan100.compose.ksp.annotations.clone.CloneDeep
import net.yan100.compose.ksp.data.ContextData
import net.yan100.compose.ksp.dsl.fileDsl

class CloneDeepVisitor : KSTopDownVisitor<ContextData, Unit>() {
  private lateinit var data: ContextData

  override fun defaultHandler(node: KSNode, data: ContextData) {
    this.data = data
  }

  @OptIn(KspExperimental::class)
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: ContextData) {
    val isCloneDeep = classDeclaration.isAnnotationPresent(CloneDeep::class)
    if (isCloneDeep) {
      val classType = classDeclaration.toClassName()
      val allProperties = classDeclaration.getAllProperties()
      fileDsl(classDeclaration.packageName.asString(), "_CloneDeep${classDeclaration.simpleName.asString()}") {
          builder.addFunction(
            FunSpec.builder("fromDeep")
              .returns(classType)
              .addParameter(ParameterSpec.builder("other", classType).build())
              .receiver(classType)
              .addCode(
                CodeBlock.builder()
                  .apply {
                    allProperties.forEach { p ->
                      val pName = p.simpleName.asString()

                      beginControlFlow("if (other.$pName != null)")
                      addStatement("this.$pName = other.$pName")
                      endControlFlow()
                    }
                  }
                  .add("return this")
                  .build()
              )
              .build()
          )
        }
        .writeTo(data.codeGenerator, Dependencies.ALL_FILES)
    }
  }
}

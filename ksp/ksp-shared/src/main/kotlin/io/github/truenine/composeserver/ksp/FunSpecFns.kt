package io.github.truenine.composeserver.ksp

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

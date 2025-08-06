package io.github.truenine.composeserver.ksp

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec

fun PropertySpec.Builder.addOpeneModifier(): PropertySpec.Builder = addModifiers(KModifier.OPEN)

fun PropertySpec.Builder.addOverrideModifier(): PropertySpec.Builder = addModifiers(KModifier.OVERRIDE)

fun PropertySpec.Builder.addFinalModifier(): PropertySpec.Builder = addModifiers(KModifier.FINAL)

fun PropertySpec.Builder.addPrivateModifier(): PropertySpec.Builder = addModifiers(KModifier.PRIVATE)

fun PropertySpec.Builder.addConstModifier(): PropertySpec.Builder = addModifiers(KModifier.CONST).mutable(false)

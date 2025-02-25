package net.yan100.compose.ksp.toolkit

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

fun TypeSpec.Builder.addOpeneModifier(): TypeSpec.Builder =
  addModifiers(KModifier.OPEN)

fun TypeSpec.Builder.addPrivateModifier(): TypeSpec.Builder =
  addModifiers(KModifier.PRIVATE)

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

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

fun TypeSpec.Builder.openedModifier(): TypeSpec.Builder = addModifiers(KModifier.OPEN)

fun TypeSpec.Builder.privateModifier(): TypeSpec.Builder = addModifiers(KModifier.PRIVATE)

fun PropertySpec.Builder.openedModifier(): PropertySpec.Builder = addModifiers(KModifier.OPEN)
fun PropertySpec.Builder.overrideModifier(): PropertySpec.Builder = addModifiers(KModifier.OVERRIDE)
fun PropertySpec.Builder.finalModifier(): PropertySpec.Builder = addModifiers(KModifier.FINAL)

fun PropertySpec.Builder.privateModifier(): PropertySpec.Builder = addModifiers(KModifier.PRIVATE)

fun PropertySpec.Builder.constantModifier(): PropertySpec.Builder = addModifiers(KModifier.CONST).mutable(false)

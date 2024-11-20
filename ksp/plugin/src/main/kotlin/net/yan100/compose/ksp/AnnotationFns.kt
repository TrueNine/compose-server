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
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KClass

inline fun <reified A : Annotation> KSAnnotation.isAnnotationBy(): Boolean {
  val c = A::class
  return shortName.getShortName() == c.simpleName && annotationType.resolve().declaration.qualifiedName?.asString() == c.qualifiedName
}

fun <A : Annotation> KSAnnotation.isAnnotationBy(annotationKClass: KClass<A>): Boolean {
  return shortName.getShortName() == annotationKClass.simpleName &&
    annotationType.resolve().declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
}

fun <A : Annotation> KSAnnotated.getKspAnnotationsByType(annotationKClass: KClass<A>): Sequence<KSAnnotation> {
  return annotations.filter {
    it.shortName.getShortName() == annotationKClass.simpleName &&
      it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
  }
}

inline fun <reified A : Annotation> KSAnnotated.getKspAnnotationsByType(): Sequence<KSAnnotation> {
  return getKspAnnotationsByType(A::class)
}

@OptIn(KspExperimental::class)
fun Sequence<KSAnnotation>.matchAnnotationByTarget(vararg targets: AnnotationTarget) = filter { ksAnno ->
  val target = ksAnno.annotationType.getAnnotationsByType(Target::class).firstOrNull()
  target?.allowedTargets?.any { it in targets } ?: false
}

@OptIn(KspExperimental::class)
fun KSAnnotation.matchAnnotationByTarget(vararg targets: AnnotationTarget): Boolean {
  val target = annotationType.getAnnotationsByType(Target::class).firstOrNull()
  return target?.allowedTargets?.any { it in targets } ?: false
}

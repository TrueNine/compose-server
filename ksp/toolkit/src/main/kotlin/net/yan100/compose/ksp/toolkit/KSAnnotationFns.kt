package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KClass

inline fun <reified A : Annotation> KSAnnotation.isAnnotationBy(): Boolean {
  val c = A::class
  return shortName.getShortName() == c.simpleName && annotationType.resolve().declaration.qualifiedName?.asString() == c.qualifiedName
}

fun <A : Annotation> KSAnnotation.isAnnotationBy(annotationKClass: KClass<A>?): Boolean {
  if (annotationKClass == null) return false

  val name = shortName.getShortName() == annotationKClass.simpleName
  val qName = annotationType.resolve().declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
  return name && qName
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

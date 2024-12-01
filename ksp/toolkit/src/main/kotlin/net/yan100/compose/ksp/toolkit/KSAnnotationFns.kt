package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KClass

inline fun <reified A : Annotation> KSAnnotation.isAnnotationByKClass(): Boolean {
  val c = A::class
  return shortName.getShortName() == c.simpleName && annotationType.resolve().declaration.qualifiedName?.asString() == c.qualifiedName
}

val KSAnnotation.simpleName get() = shortName.getShortName()

fun KSAnnotation.isAnnotationByKClassQualifiedName(qualifiedName: String): Boolean {
  if (qualifiedName.isBlank()) return false
  val simpleName = qualifiedName.substringAfterLast(".")
  val name = this.simpleName == simpleName
  val qName = resolvedDeclaration.qualifiedNameAsString == qualifiedName
  return name && qName
}

fun <A : Annotation> KSAnnotation.isAnnotationByKClass(annotationKClass: KClass<A>): Boolean {
  val qName = annotationKClass.qualifiedName
  if (qName.isNullOrBlank()) return false
  return isAnnotationByKClassQualifiedName(qName)
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

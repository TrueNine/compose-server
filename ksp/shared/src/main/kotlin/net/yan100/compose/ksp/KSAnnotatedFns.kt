package net.yan100.compose.ksp

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import kotlin.reflect.KClass

fun KSAnnotated.getKsAnnotationsByAnnotationClassQualifiedName(
  qualifiedName: String
): Sequence<KSAnnotation> {
  return annotations.filter {
    it.annotationType.fastResolve().declaration.qualifiedName?.asString() ==
      qualifiedName
  }
}

fun <A : Annotation> KSAnnotated.getKsAnnotationsByAnnotationClass(
  annotationKClass: KClass<A>
): Sequence<KSAnnotation> {
  return getKsAnnotationsByAnnotationClassQualifiedName(
    annotationKClass.qualifiedName!!
  )
}

/**
 * annotationType.resolve().declaration
 *
 * @see KSAnnotation.annotationType
 */
val KSAnnotation.resolvedDeclaration: KSDeclaration
  get() {
    return annotationType.fastResolve().declaration
  }

inline fun <reified A : Annotation> KSAnnotated
  .getKsAnnotationsByAnnotationClass(): Sequence<KSAnnotation> {
  return getKsAnnotationsByAnnotationClass(A::class)
}

package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KClass

fun KSAnnotated.getKsAnnotationsByAnnotationClassQualifiedName(qualifiedName: String): Sequence<KSAnnotation> {
  return annotations.filter {
    it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName
  }
}


fun <A : Annotation> KSAnnotated.getKsAnnotationsByAnnotationClass(annotationKClass: KClass<A>): Sequence<KSAnnotation> {
  return getKsAnnotationsByAnnotationClassQualifiedName(annotationKClass.qualifiedName!!)
}

inline fun <reified A : Annotation> KSAnnotated.getKsAnnotationsByAnnotationClass(): Sequence<KSAnnotation> {
  return getKsAnnotationsByAnnotationClass(A::class)
}
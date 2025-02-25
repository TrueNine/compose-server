package net.yan100.compose.ksp.toolkit

import com.squareup.kotlinpoet.AnnotationSpec

fun AnnotationSpec.Builder.useFile() =
  useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)

fun AnnotationSpec.Builder.useGet() =
  useSiteTarget(AnnotationSpec.UseSiteTarget.GET)

fun AnnotationSpec.Builder.useSet() =
  useSiteTarget(AnnotationSpec.UseSiteTarget.SET)

fun AnnotationSpec.Builder.useField() =
  useSiteTarget(AnnotationSpec.UseSiteTarget.FIELD)

fun AnnotationSpec.Builder.useDelegate() =
  useSiteTarget(AnnotationSpec.UseSiteTarget.DELEGATE)

fun AnnotationSpec.Builder.useProperty() =
  useSiteTarget(AnnotationSpec.UseSiteTarget.PROPERTY)

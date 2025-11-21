package io.github.truenine.composeserver.ksp.models

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

data class ClassDefine(val packageName: String, val className: String) {

  /** kotlinpoet class name */
  fun toClassName(): ClassName {
    return ClassName(packageName, className)
  }

  /** Class or other fully qualified name */
  val qualifiedName: String = arrayOf(packageName, className).joinToString(".")

  fun toAnnotationSpec(): AnnotationSpec {
    return toAnnotationSpecBuilder().build()
  }

  fun toAnnotationSpecBuilder(): AnnotationSpec.Builder {
    return AnnotationSpec.builder(this.toClassName())
  }
}

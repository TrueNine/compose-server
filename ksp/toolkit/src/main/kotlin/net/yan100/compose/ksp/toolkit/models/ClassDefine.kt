package net.yan100.compose.ksp.toolkit.models

import com.squareup.kotlinpoet.ClassName

data class ClassDefine(
  val packageName: String,
  val className: String
) {

  /**
   * kotlinpoet class name
   */
  fun toClassName(): ClassName {
    return ClassName(packageName, className)
  }
}

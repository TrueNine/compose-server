package net.yan100.compose.ksp.ksp.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName

class CodeFileDsl @JvmOverloads constructor(
  packageName: String = "",
  fileName: String = "",
  className: ClassName? = null,
  memberName: MemberName? = null
) : StandardBuilderAdaptor<FileSpec.Builder, FileSpec> {
  private val fileBuilder: FileSpec.Builder = if (null != className) FileSpec.builder(className)
  else if (null != memberName) FileSpec.builder(memberName)
  else FileSpec.builder(packageName, fileName)

  fun classType(className: ClassName, classDsl: ClassDsl.() -> Unit) =
    builder.addType(ClassDsl(className = className).apply(classDsl).build())


  override val builder: FileSpec.Builder = fileBuilder
  override fun build(): FileSpec = fileBuilder.build()
}

fun fileDsl(packageName: String, fileName: String, receiver: CodeFileDsl.() -> Unit): FileSpec {
  val fb = CodeFileDsl(packageName, fileName)
  receiver(fb)
  return fb.build()
}


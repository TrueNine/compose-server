package net.yan100.compose.ksp.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import kotlin.reflect.KClass

class KFileSpecScope
@JvmOverloads
constructor(
  packageName: String = "",
  fileName: String = "",
  className: ClassName? = null,
  memberName: MemberName? = null,
) : StandardBuilderAdaptor<FileSpec.Builder, FileSpec> {
  private val fb: FileSpec.Builder =
    if (null != className) FileSpec.builder(className)
    else if (null != memberName) FileSpec.builder(memberName)
    else FileSpec.builder(packageName, fileName)

  fun annotateBy(cls: KClass<*>) = builder.addAnnotation(cls)

  fun classBy(
    className: ClassName,
    KClassSpecScope: KClassSpecScope.() -> Unit,
  ) =
    builder.addType(
      KClassSpecScope(className = className, fileBuilder = fb)
        .apply(KClassSpecScope)
        .build()
    )

  override val fileBuilder = fb
  override val builder: FileSpec.Builder = fileBuilder

  override fun build(): FileSpec = fileBuilder.build()
}

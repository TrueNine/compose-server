package net.yan100.compose.ksp.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

class KObjectSpecScope
@JvmOverloads
constructor(
  name: String = "",
  className: ClassName? = null,
  override val fileBuilder: FileSpec.Builder,
) : StandardBuilderAdaptor<TypeSpec.Builder, TypeSpec> {
  override val builder: TypeSpec.Builder =
    if (null != className) TypeSpec.objectBuilder(className)
    else TypeSpec.classBuilder(name)

  override fun build(): TypeSpec = builder.build()
}

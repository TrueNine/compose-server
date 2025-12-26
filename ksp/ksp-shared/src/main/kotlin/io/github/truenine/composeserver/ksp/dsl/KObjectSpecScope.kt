package io.github.truenine.composeserver.ksp.dsl

import com.squareup.kotlinpoet.*

class KObjectSpecScope @JvmOverloads constructor(name: String = "", className: ClassName? = null, override val fileBuilder: FileSpec.Builder) :
  StandardBuilderAdaptor<TypeSpec.Builder, TypeSpec> {
  override val builder: TypeSpec.Builder = if (null != className) TypeSpec.objectBuilder(className) else TypeSpec.classBuilder(name)

  override fun build(): TypeSpec = builder.build()
}

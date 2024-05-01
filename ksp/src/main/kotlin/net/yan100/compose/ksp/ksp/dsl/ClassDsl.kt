package net.yan100.compose.ksp.ksp.dsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

class ClassDsl @JvmOverloads constructor(
  private val name: String = "",
  private val className: ClassName? = null,
) : StandardBuilderAdaptor<TypeSpec.Builder, TypeSpec> {
  private val cb: TypeSpec.Builder = if (null != className) TypeSpec.classBuilder(className)
  else TypeSpec.classBuilder(name)

  fun extendsBy(superClass: KClass<*>) = cb.superclass(superClass)
  fun extendsBy(typeName: TypeName) = cb.superclass(typeName)

  // TODO CodeBlock Dsl
  fun implBy(superInterface: KClass<*>) = cb.addSuperinterface(superInterface)
  fun implBy(typeName: TypeName) = cb.addSuperinterface(typeName)


  fun property(propertySpec: PropertySpec) = cb.addProperty(propertySpec)
  fun kdoc(f: KDocDsl.() -> Unit) {
    cb.addKdoc(
      KDocDsl().apply(f).build()
    )
  }


  fun type(typeSpec: TypeSpec) = cb.addType(typeSpec)

  override val builder: TypeSpec.Builder = cb
  override fun build(): TypeSpec = cb.build()
}

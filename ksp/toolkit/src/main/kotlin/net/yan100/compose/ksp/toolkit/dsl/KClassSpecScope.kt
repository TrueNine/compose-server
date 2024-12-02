/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.ksp.toolkit.dsl

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

class KClassSpecScope @JvmOverloads constructor(name: String = "", className: ClassName? = null, override val fileBuilder: FileSpec.Builder) :
  StandardBuilderAdaptor<TypeSpec.Builder, TypeSpec> {
  override val builder = if (null != className) TypeSpec.classBuilder(className) else TypeSpec.classBuilder(name)

  fun opened() = builder.addModifiers(KModifier.OPEN)

  fun annotateBy(cls: KClass<*>) = builder.addAnnotation(cls)

  fun annotateBy(spec: AnnotationSpec) = builder.addAnnotation(spec)

  fun annotateAllBy(specs: Iterable<AnnotationSpec>) = builder.addAnnotations(specs)

  fun extendsClassBy(superClass: KClass<*>) = builder.superclass(superClass)
  fun extendsClassBy(typeName: TypeName) = builder.superclass(typeName)
  fun extendsInterfaceBy(typeName: TypeName) = builder.addSuperinterface(typeName)
  fun extendsInterfaceBy(superClass: KClass<*>) = builder.addSuperinterface(superClass)

  // TODO CodeBlock Dsl
  fun implBy(superInterface: KClass<*>) = builder.addSuperinterface(superInterface)

  fun implBy(typeName: TypeName) = builder.addSuperinterface(typeName)

  fun property(propertySpec: PropertySpec) = builder.addProperty(propertySpec)

  fun kdoc(f: KDocSpecScope.() -> Unit) {
    builder.addKdoc(KDocSpecScope(fileBuilder).apply(f).build())
  }

  fun type(typeSpec: TypeSpec) = builder.addType(typeSpec)

  override fun build() = builder.build()
}

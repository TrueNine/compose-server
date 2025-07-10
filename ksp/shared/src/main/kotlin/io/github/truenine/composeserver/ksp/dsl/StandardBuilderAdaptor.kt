package io.github.truenine.composeserver.ksp.dsl

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import kotlin.reflect.KClass

interface StandardBuilderAdaptor<T, R> {
  val builder: T

  fun build(): R

  val fileBuilder: FileSpec.Builder

  fun importBy(pkg: String, vararg names: String) = fileBuilder.addImport(pkg, *names)

  fun importBy(classDeclaration: KSClassDeclaration) = fileBuilder.addImport(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())

  fun importBy(clazz: Class<*>) = fileBuilder.addImport(clazz)

  fun importBy(kClazz: KClass<*>) = fileBuilder.addImport(kClazz)
}

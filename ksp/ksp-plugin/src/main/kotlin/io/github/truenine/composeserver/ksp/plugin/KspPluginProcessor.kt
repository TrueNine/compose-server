package io.github.truenine.composeserver.ksp.plugin

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import io.github.truenine.composeserver.ksp.meta.annotations.MetaDef
import io.github.truenine.composeserver.ksp.meta.annotations.MetaSkipGeneration
import io.github.truenine.composeserver.ksp.models.DeclarationContext
import io.github.truenine.composeserver.ksp.plugin.visitor.JpaNameClassVisitor
import io.github.truenine.composeserver.ksp.simpleNameAsString

class KspPluginProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
  private fun <D : KSDeclaration> getCtxData(declaration: D, resolver: Resolver): DeclarationContext<D> {
    return DeclarationContext(declaration, environment, resolver)
  }

  @OptIn(KspExperimental::class)
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val nextSymbols = mutableSetOf<KSAnnotated>()
    val options = environment.options
    val enableJpa = options["io.github.truenine.composeserver.ksp.plugin.generateJpa"]?.toBooleanStrictOrNull() == true

    if (enableJpa) nextSymbols += jpaGenerate(resolver)

    return nextSymbols.toList()
  }

  @OptIn(KspExperimental::class)
  fun jpaGenerate(resolver: Resolver): Sequence<KSAnnotated> {
    val lis =
      resolver.getSymbolsWithAnnotation("jakarta.persistence.EntityListener").filterIsInstance<KSDeclaration>().firstOrNull()?.let {
        it.annotations.firstOrNull()?.toAnnotationSpec()
      }
    val jpaSymbols = resolver.getSymbolsWithAnnotation("io.github.truenine.composeserver.ksp.meta.annotations.MetaDef")
    jpaSymbols
      .filter { it.validate() }
      .filterIsInstance<KSClassDeclaration>()
      .filter { !it.isAnnotationPresent(MetaSkipGeneration::class) }
      .filter { it.getDeclaredProperties().toList().isNotEmpty() }
      .filter { !it.isCompanionObject }
      .filter { it.simpleNameAsString.startsWith("Super") }
      .filterNot { it.simpleNameAsString.contains("$") }
      .filter { it.getAnnotationsByType(MetaDef::class).toList().isNotEmpty() }
      .forEach { getCtxData(it, resolver).accept(JpaNameClassVisitor(lis)) }
    return resolver.getSymbolsWithAnnotation("io.github.truenine.composeserver.ksp.meta.annotations.MetaDef").filter { !it.validate() }
  }
}

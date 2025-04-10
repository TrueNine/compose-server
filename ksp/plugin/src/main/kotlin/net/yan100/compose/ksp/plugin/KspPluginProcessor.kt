package net.yan100.compose.ksp.plugin

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import net.yan100.compose.ksp.models.DeclarationContext
import net.yan100.compose.ksp.plugin.visitor.JpaNameClassVisitor
import net.yan100.compose.ksp.plugin.visitor.RepositoryIPageExtensionsVisitor
import net.yan100.compose.ksp.simpleNameAsString
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaSkipGeneration

class KspPluginProcessor(private val environment: SymbolProcessorEnvironment) :
  SymbolProcessor {
  private fun <D : KSDeclaration> getCtxData(
    declaration: D,
    resolver: Resolver,
  ): DeclarationContext<D> {
    return DeclarationContext(declaration, environment, resolver)
  }

  @OptIn(KspExperimental::class)
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val nextSymbols = mutableSetOf<KSAnnotated>()
    val options = environment.options
    val enableJpa =
      options["net.yan100.compose.ksp.plugin.generateJpa"]
        ?.toBooleanStrictOrNull() == true

    if (enableJpa) nextSymbols += jpaGenerate(resolver)

    resolver
      .getPackagesWithAnnotation("org.springframework.stereotype.Repository")
      .filterIsInstance<KSClassDeclaration>()
      .filter { it.classKind == ClassKind.INTERFACE }
      .forEach {
        getCtxData(it, resolver).accept(RepositoryIPageExtensionsVisitor())
      }
    return nextSymbols.toList()
  }

  @OptIn(KspExperimental::class)
  fun jpaGenerate(resolver: Resolver): Sequence<KSAnnotated> {
    val lis =
      resolver
        .getSymbolsWithAnnotation("jakarta.persistence.EntityListener")
        .filterIsInstance<KSDeclaration>()
        .firstOrNull()
        ?.let { it.annotations.firstOrNull()?.toAnnotationSpec() }
    val jpaSymbols =
      resolver.getSymbolsWithAnnotation(
        "net.yan100.compose.meta.annotations.MetaDef"
      )
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
    return resolver
      .getSymbolsWithAnnotation("net.yan100.compose.meta.annotations.MetaDef")
      .filter { !it.validate() }
  }
}

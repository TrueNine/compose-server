package net.yan100.compose.ksp.visitor

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.ksp.toolkit.*
import net.yan100.compose.ksp.toolkit.dsl.fileDsl
import net.yan100.compose.ksp.toolkit.models.DeclarationContext

private const val pageAnnotationPath = "org.springframework.data.domain.Page"
private const val pageableAnnotationPath = "org.springframework.data.domain.Pageable"

class RepositoryIPageExtensionsVisitor : KSTopDownVisitor<DeclarationContext<KSClassDeclaration>, Unit>() {
  private lateinit var log: KSPLogger

  override fun defaultHandler(node: KSNode, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
  }

  private fun supported(declaration: KSDeclaration): List<KSFunctionDeclaration> {
    if (declaration !is KSClassDeclaration) return emptyList()
    if (declaration.classKind != ClassKind.INTERFACE) return emptyList()
    return declaration.getDeclaredFunctions().filter { f ->
      val rtType = f.returnType
      if (rtType?.resolve()?.declaration?.isClassQualifiedName(pageAnnotationPath) == false) return@filter false
      val p = f.parameters
      if (p.isEmpty()) return@filter false
      val hasPageable = p.any {
        it.type.resolve().declaration.isClassQualifiedName(pageableAnnotationPath)
      }
      if (!hasPageable) {
        log.warn("${f.qualifiedNameAsStringStr} return Page, but no Pageable parameter found.")
        return@filter false
      }
      true
    }.toList()
  }

  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: DeclarationContext<KSClassDeclaration>) {
    val list = supported(classDeclaration)
    if (list.isEmpty()) return
    var hasError = false
    fileDsl(classDeclaration.packageName.asString(), "${classDeclaration.simpleNameGetShortNameStr}PageExtensionsFunctions") {
      builder.addAnnotation(
        AnnotationSpec.builder(
          Suppress::class
        ).addMember("%S", "Unused").addMember("%S", "RedundantVisibilityModifier").useFile().build()
      )
      var imported = false
      fun importExt() {
        if (imported) return
        builder.addImport("net.yan100.compose.rds.core", "toIPage", "toPageable")
        imported = true
      }

      list.forEach { pageFn ->
        val g = pageFn.returnType?.resolve()?.arguments?.firstOrNull()
        if (g?.type?.resolve()?.isError == true) {
          hasError = true
          return@fileDsl
        }
        val returnTypeGeneric = g?.toTypeName()

        if (returnTypeGeneric == null || returnTypeGeneric.isNullable) {
          log.error("${pageFn.qualifiedNameAsStringStr} but nullable generic, not generate extension function")
          return@forEach
        }
        importExt()
        // 替换参数
        val replaceParameters = pageFn.parameters.map { p ->
          val isPageable = p.type.resolve().declaration.isClassQualifiedName(pageableAnnotationPath)
          if (isPageable) {
            ParameterSpec.builder("pq", Pq::class)
              .defaultValue("%T.DEFAULT_MAX", Pq::class)
              .build()
          } else ParameterSpec.builder(p.name!!.asString(), p.type.toTypeName()).build()
        }

        val extensionFunction = FunSpec.builder(pageFn.simpleNameAsStringStr)
          .addParameters(replaceParameters)
          .returns(Pr::class.asClassName().parameterizedBy(returnTypeGeneric))
          .receiver(classDeclaration.toClassName())
          .also {
            it.addStatement(
              "return this.%N(%L).toIPage()",
              pageFn.simpleNameAsStringStr,
              replaceParameters.joinToString { p -> if (p.name == "pq") "pq.toPageable()" else p.name })// Page<*>::toIPage
          }
          .build()
        builder.addFunction(extensionFunction)
      }
      // 用代码输出文件内容
    }.writeTo(data.codeGenerator, Dependencies.ALL_FILES)
    if (hasError) data.report(classDeclaration)
  }
}

package io.github.truenine.composeserver.ksp.models

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitor
import java.util.concurrent.CopyOnWriteArraySet

data class DeclarationContext<D : KSDeclaration>(
  val declaration: D,
  val environment: SymbolProcessorEnvironment,
  val resolver: Resolver,
  val codeGenerator: CodeGenerator = environment.codeGenerator,
  val log: KSPLogger = environment.logger,
  val file: KSFile = declaration.containingFile!!,
  var dependencies: Dependencies = Dependencies.ALL_FILES,
  private val notProcessReportList: MutableSet<KSAnnotated> = CopyOnWriteArraySet(),
) {
  /** 调用 declaration 的 accept 方法 */
  fun <R : Any?> accept(visitor: KSVisitor<DeclarationContext<D>, R>): R {
    return declaration.accept(visitor = visitor, data = this)
  }

  fun clearReported() {
    notProcessReportList.clear()
  }

  fun report(annotated: KSAnnotated) {
    notProcessReportList += annotated
  }
}

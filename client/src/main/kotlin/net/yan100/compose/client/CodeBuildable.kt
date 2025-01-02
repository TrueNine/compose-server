package net.yan100.compose.client

import net.yan100.compose.client.domain.TsModifier
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsScopeQuota
import net.yan100.compose.client.domain.TsTypeModifier
import net.yan100.compose.client.domain.entries.TsFile
import net.yan100.compose.client.domain.entries.TsImport

class CodeBuildable<T : TsFile<T>>(
  private val builder: Appendable = StringBuilder(),
  val indentCount: Int = 2,
  val indentChar: Char = ' ',
  private val indent: String = indentChar.toString().repeat(indentCount)
) : Appendable by builder {
  private var dirtyCount: Int = 0
  private val indentStr get() = if (dirtyCount > 0) indent.repeat(dirtyCount) else ""

  private fun dirty(block: () -> Unit) {
    dirtyCount += 1
    block()
    dirtyCount -= 1
  }

  fun imports(imports: List<TsImport>) {
    if (imports.isEmpty()) return
    line(imports.toRenderCode())
    line()
  }

  fun space() {
    append(" ")
  }

  fun space(tsTypeModifier: TsTypeModifier) {
    code(tsTypeModifier.marker)
    space()
  }

  fun space(tsModifier: TsModifier) {
    space(tsModifier.modifier)
  }

  fun space(code: String) {
    if (code.isNotBlank()) {
      code(code)
      space()
    }
  }

  fun line() = appendLine()

  fun line(code: String) {
    if (code.isNotBlank()) {
      code.lines().filter { it.isNotBlank() }.forEach {
        code(indentStr)
        code(it.trim())
        line()
      }
    }
  }

  fun code(code: String) {
    if (code.isNotEmpty()) append(code)
  }

  fun <S : TsScope<S>> exportScope(scope: S, block: CodeBuildable<T>.(scope: S) -> Unit) {
    space(TsModifier.Export)
    scope(scope) {
      block(this, scope)
    }
  }

  fun scope(scope: TsScope<*>, block: CodeBuildable<T>.(scope: TsScope<*>) -> Unit) {
    val name = scope.name.toVariableName()
    space(scope.modifier)
    space(name)
    val generics = when (scope) {
      is TsScope.Interface -> scope.generics
      is TsScope.TypeAlias -> scope.generics
      is TsScope.Class -> scope.generics
      is TsScope.Enum -> emptyList()
      is TsScope.TypeVal -> emptyList()
    }
    val superTypes = when (scope) {
      is TsScope.Class -> if (scope.superTypes.isNotEmpty()) scope.superTypes else emptyList()
      is TsScope.Interface -> if (scope.superTypes.isNotEmpty()) scope.superTypes else emptyList()
      else -> emptyList()
    }
    if (generics.isNotEmpty()) space(generics.toRenderCode())
    if (superTypes.isNotEmpty()) {
      val superTypeNames = superTypes.joinToString(separator = ", ") { superType ->
        superType.toString()
      }
      space(TsModifier.Extends)
      space(superTypeNames)
    }
    scope(scope.scopeQuota) { block(this, scope) }
  }

  fun scope(scopeQuota: TsScopeQuota = TsScopeQuota.BLANK, block: CodeBuildable<T>.() -> Unit) {
    line(scopeQuota.left)
    dirty { block(this) }
    line(scopeQuota.right)
  }

  override fun toString(): String = builder.toString()
}
